/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

/*
 * -*- c++ -*-
 *
 * (C) Copyright IBM Corp. and others 2013 - All Rights Reserved
 *
 * Range checking
 *
 */

#ifndef __LETABLEREFERENCE_H
#define __LETABLEREFERENCE_H

#include "LETypes.h"
#include "LEFontInstance.h"


#define kQuestionmarkTableTag  0x3F3F3F3FUL
#define kTildeTableTag  0x7e7e7e7eUL
#ifdef __cplusplus

// internal - interface for range checking
U_NAMESPACE_BEGIN

#if LE_ASSERT_BAD_FONT
class LETableReference; // fwd
/**
 *  defined in OpenTypeUtilities.cpp
 * @internal
 */
U_INTERNAL void U_EXPORT2 _debug_LETableReference(const char *f, int l, const char *msg, const LETableReference *what, const void *ptr, size_t len);

#define LE_DEBUG_TR(x) _debug_LETableReference(__FILE__, __LINE__, x, this, NULL, 0);
#define LE_DEBUG_TR3(x,y,z) _debug_LETableReference(__FILE__, __LINE__, x, this, (const void*)y, (size_t)z);
#if 0
#define LE_TRACE_TR(x) _debug_LETableReference(__FILE__, __LINE__, x, this, NULL, 0);
#else
#define LE_TRACE_TR(x)
#endif

#else
#define LE_DEBUG_TR(x)
#define LE_DEBUG_TR3(x,y,z)
#define LE_TRACE_TR(x)
#endif

/**
 * @internal
 */
class LETableReference {
public:
/**
 * @internal
 * Construct from a specific tag
 */
  LETableReference(const LEFontInstance* font, LETag tableTag, LEErrorCode &success) :
    fFont(font), fTag(tableTag), fParent(NULL), fStart(NULL),fLength(LE_UINTPTR_MAX) {
      loadTable(success);
    LE_TRACE_TR("INFO: new table load")
  }

  LETableReference(const LETableReference &parent, LEErrorCode &success) : fFont(parent.fFont), fTag(parent.fTag), fParent(&parent), fStart(parent.fStart), fLength(parent.fLength) {
    if(LE_FAILURE(success)) {
      clear();
    }
    LE_TRACE_TR("INFO: new clone")
  }

   LETableReference(const le_uint8* data, size_t length = LE_UINTPTR_MAX) :
    fFont(NULL), fTag(kQuestionmarkTableTag), fParent(NULL), fStart(data), fLength(length) {
    LE_TRACE_TR("INFO: new raw")
  }
  LETableReference() :
    fFont(NULL), fTag(kQuestionmarkTableTag), fParent(NULL), fStart(NULL), fLength(0) {
    LE_TRACE_TR("INFO: new empty")
  }

  ~LETableReference() {
    fTag=kTildeTableTag;
    LE_TRACE_TR("INFO: new dtor")
  }

  /**
   * @internal
   * @param length  if LE_UINTPTR_MAX means "whole table"
   * subset
   */
  LETableReference(const LETableReference &parent, size_t offset, size_t length,
                   LEErrorCode &err) :
    fFont(parent.fFont), fTag(parent.fTag), fParent(&parent),
    fStart((parent.fStart)+offset), fLength(length) {
    if(LE_SUCCESS(err)) {
      if(isEmpty()) {
        //err = LE_MISSING_FONT_TABLE_ERROR;
        clear(); // it's just empty. Not an error.
      } else if(offset >= fParent->fLength) {
        LE_DEBUG_TR3("offset out of range: (%p) +%d", NULL, offset);
        err = LE_INDEX_OUT_OF_BOUNDS_ERROR;
        clear();
      } else {
        if(fLength == LE_UINTPTR_MAX &&
           fParent->fLength != LE_UINTPTR_MAX) {
          fLength = (fParent->fLength) - offset; // decrement length as base address is incremented
        }
        if(fLength != LE_UINTPTR_MAX) {  // if we have bounds:
          if(offset+fLength > fParent->fLength) {
            LE_DEBUG_TR3("offset+fLength out of range: (%p) +%d", NULL, offset+fLength);
            err = LE_INDEX_OUT_OF_BOUNDS_ERROR; // exceeded
            clear();
          }
        }
      }
    } else {
      clear();
    }
    LE_TRACE_TR("INFO: new subset")
  }

  const void* getAlias() const { return (const void*)fStart; }
  const void* getAliasTODO() const { LE_DEBUG_TR("getAliasTODO()"); return (const void*)fStart; }
  le_bool isEmpty() const { return fStart==NULL || fLength==0; }
  le_bool isValid() const { return !isEmpty(); }
  le_bool hasBounds() const { return fLength!=LE_UINTPTR_MAX; }
  void clear() { fLength=0; fStart=NULL; }
  size_t getLength() const { return fLength; }
  const LEFontInstance* getFont() const { return fFont; }
  LETag getTag() const { return fTag; }
  const LETableReference* getParent() const { return fParent; }

  void addOffset(size_t offset, LEErrorCode &success) {
    if(hasBounds()) {
      if(offset > fLength) {
        LE_DEBUG_TR("addOffset off end");
        success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
        return;
      } else {
        fLength -= offset;
      }
    }
    fStart += offset;
  }

  size_t ptrToOffset(const void *atPtr, LEErrorCode &success) const {
    if(atPtr==NULL) return 0;
    if(LE_FAILURE(success)) return LE_UINTPTR_MAX;
    if((atPtr < fStart) ||
       (hasBounds() && (atPtr > fStart+fLength))) {
      LE_DEBUG_TR3("ptrToOffset args out of range: %p", atPtr, 0);
      success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
      return LE_UINTPTR_MAX;
    }
    return ((const le_uint8*)atPtr)-fStart;
  }

  /**
   * Clamp down the length, for range checking.
   */
  size_t contractLength(size_t newLength) {
    if(fLength!=LE_UINTPTR_MAX&&newLength>0&&newLength<=fLength) {
      fLength = newLength;
    }
    return fLength;
  }

  /**
   * Throw an error if offset+length off end
   */
public:
  size_t verifyLength(size_t offset, size_t length, LEErrorCode &success) {
    if(isValid()&&
       LE_SUCCESS(success) &&
       fLength!=LE_UINTPTR_MAX && length!=LE_UINTPTR_MAX && offset!=LE_UINTPTR_MAX &&
       (offset+length)>fLength) {
      LE_DEBUG_TR3("verifyLength failed (%p) %d",NULL, offset+length);
      success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
#if LE_ASSERT_BAD_FONT
      fprintf(stderr, "offset=%lu, len=%lu, would be at %p, (%lu) off end. End at %p\n", offset,length, fStart+offset+length, (offset+length-fLength), (offset+length-fLength)+fStart);
#endif
    }
    return fLength;
  }

  le_bool isSubsetOf(const LETableReference& base) const {
    if(this == &base) return true;
    if(fStart < base.fStart) return false;
    if(base.hasBounds()) {
      if(fStart >= base.fStart + base.fLength) return false;
      if(hasBounds()) {
        if(fStart + fLength > base.fStart + base.fLength) return false;
      }
    }
    return true;
  }

  /**
   * Change parent link to another
   */
  LETableReference &reparent(const LETableReference &base) {
    fParent = &base;
    return *this;
  }

  /**
   * remove parent link. Factory functions should do this.
   */
  void orphan(void) {
    fParent=NULL;
  }

protected:
  const LEFontInstance* fFont;
  LETag  fTag;
  const LETableReference *fParent;
  const le_uint8 *fStart; // keep as 8 bit internally, for pointer math
  size_t fLength;

  void loadTable(LEErrorCode &success) {
    if(LE_SUCCESS(success)) {
      fStart = (const le_uint8*)(fFont->getFontTable(fTag, fLength)); // note - a null table is not an error.
    }
  }

  void setRaw(const void *data, size_t length = LE_UINTPTR_MAX) {
    fFont = NULL;
    fTag = kQuestionmarkTableTag;
    fParent = NULL;
    fStart = (const le_uint8*)data;
    fLength = length;
  }
};


template<class T>
class LETableVarSizer {
 public:
  inline static size_t getSize();
};

// base definition- could override for adjustments
template<class T> inline
size_t LETableVarSizer<T>::getSize() {
  return sizeof(T);
}

/**
 * \def LE_VAR_ARRAY
 * @param x Type (T)
 * @param y some member that is of length ANY_NUMBER
 * Call this after defining a class, for example:
 *   LE_VAR_ARRAY(FeatureListTable,featureRecordArray)
 * this is roughly equivalent to:
 *   template<> inline size_t LETableVarSizer<FeatureListTable>::getSize() { return sizeof(FeatureListTable) - (sizeof(le_uint16)*ANY_NUMBER); }
 * it's a specialization that informs the LETableReference subclasses to NOT include the variable array in the size.
 * dereferencing NULL is valid here because we never actually dereference it, just inside sizeof.
 */
#define LE_VAR_ARRAY(x,y) template<> inline size_t LETableVarSizer<x>::getSize() { return sizeof(x) - (sizeof(((const x*)0)->y)); }

/**
 * Open a new entry based on an existing table
 */

/**
 * \def LE_UNBOUNDED_ARRAY
 * define an array with no *known* bound. Will trim to available size.
 * @internal
 */
#define LE_UNBOUNDED_ARRAY LE_UINT32_MAX

template<class T>
class LEReferenceToArrayOf : public LETableReference {
public:
  LEReferenceToArrayOf(const LETableReference &parent, LEErrorCode &success, size_t offset, le_uint32 count)
    : LETableReference(parent, offset, LE_UINTPTR_MAX, success), fCount(count) {
    LE_TRACE_TR("INFO: new RTAO by offset")
    if(LE_SUCCESS(success)) {
      if(count == LE_UNBOUNDED_ARRAY) { // not a known length
        count = getLength()/LETableVarSizer<T>::getSize(); // fit to max size
      }
      LETableReference::verifyLength(0, LETableVarSizer<T>::getSize()*count, success);
    }
    if(LE_FAILURE(success)) {
      fCount=0;
      clear();
    }
  }

  LEReferenceToArrayOf(const LETableReference &parent, LEErrorCode &success, const T* array, le_uint32 count)
    : LETableReference(parent, parent.ptrToOffset(array, success), LE_UINTPTR_MAX, success), fCount(count) {
LE_TRACE_TR("INFO: new RTAO")
    if(LE_SUCCESS(success)) {
      if(count == LE_UNBOUNDED_ARRAY) { // not a known length
        count = getLength()/LETableVarSizer<T>::getSize(); // fit to max size
      }
      LETableReference::verifyLength(0, LETableVarSizer<T>::getSize()*count, success);
    }
    if(LE_FAILURE(success)) clear();
  }
 LEReferenceToArrayOf(const LETableReference &parent, LEErrorCode &success, const T* array, size_t offset, le_uint32 count)
   : LETableReference(parent, parent.ptrToOffset(array, success)+offset, LE_UINTPTR_MAX, success), fCount(count) {
LE_TRACE_TR("INFO: new RTAO")
    if(LE_SUCCESS(success)) {
      if(count == LE_UNBOUNDED_ARRAY) { // not a known length
        count = getLength()/LETableVarSizer<T>::getSize(); // fit to max size
      }
      LETableReference::verifyLength(0, LETableVarSizer<T>::getSize()*count, success);
    }
    if(LE_FAILURE(success)) clear();
  }

 LEReferenceToArrayOf() :LETableReference(), fCount(0) {}

  le_uint32 getCount() const { return fCount; }

  using LETableReference::getAlias;

  const T *getAlias(le_uint32 i, LEErrorCode &success) const {
    if(LE_SUCCESS(success)&& i<getCount()) {
      return  ((const T*)getAlias())+i;
    } else {
      if(LE_SUCCESS(success)) {
        LE_DEBUG_TR("getAlias(subscript) out of range");
        success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
      }
      return ((const T*)getAlias());  // return first item, so there's no crash
    }
  }

  const T *getAliasTODO() const { LE_DEBUG_TR("getAliasTODO<>"); return (const T*)fStart; }

  const T& getObject(le_uint32 i, LEErrorCode &success) const {
    return *getAlias(i,success);
  }

  const T& operator()(le_uint32 i, LEErrorCode &success) const {
    return *getAlias(i,success);
  }

  size_t getOffsetFor(le_uint32 i, LEErrorCode &success) const {
    if(LE_SUCCESS(success)&&i<getCount()) {
      return LETableVarSizer<T>::getSize()*i;
    } else {
      success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
    }
    return 0;
  }

  LEReferenceToArrayOf<T> &reparent(const LETableReference &base) {
    fParent = &base;
    return *this;
  }

 LEReferenceToArrayOf(const LETableReference& parent, LEErrorCode & success) : LETableReference(parent,0, LE_UINTPTR_MAX, success), fCount(0) {
    LE_TRACE_TR("INFO: null RTAO")
  }

  /**
   * set this to point within our fParent, but based on 'base' as a subtable.
   */
  void setToOffsetInParent(const LETableReference& base, size_t offset, le_uint32 count, LEErrorCode &success) {
LE_TRACE_TR("INFO: sTOIP")
    if(LE_FAILURE(success)) return;
    if(!fParent->isSubsetOf(base)) {  // Ensure that 'base' is containable within our parent.
      clear();                        // otherwise, it's not a subtable of our parent.
      LE_DEBUG_TR("setToOffsetInParents called on non subsets");
      success = LE_ILLEGAL_ARGUMENT_ERROR; return;
    }
    size_t baseOffset = fParent->ptrToOffset(((const le_uint8*)base.getAlias())+offset, success);
    if(LE_FAILURE(success)) return; // base was outside of parent's range
    if(fParent->hasBounds()) {
      if((baseOffset >= fParent->getLength()) || // start off end of parent
         (baseOffset+(count*LETableVarSizer<T>::getSize()) >= fParent->getLength()) || // or off end of parent
         count > LE_UINTPTR_MAX/LETableVarSizer<T>::getSize()) { // or more than would fit in memory
        LE_DEBUG_TR("setToOffsetInParent called with bad length");
        success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
        clear();
        return; // start would go off end of parent
      }
    }
    fStart = (const le_uint8*)(fParent->getAlias()) + baseOffset;
    //fLength = count*LETableVarSizer<T>::getSize(); - no- do not shrink fLength.
    if(fParent->hasBounds()) {
      fLength = (fParent->getLength() - (fStart-(const le_uint8*)fParent->getAlias())); // reduces fLength accordingly.
    } else {
      fLength = LE_UINTPTR_MAX; // unbounded
    }
    if((fStart < fParent->getAlias()) ||
       (hasBounds()&&(fStart+fLength < fStart))) { // wrapped
        LE_DEBUG_TR("setToOffsetInParent called with bad length");
        success = LE_INDEX_OUT_OF_BOUNDS_ERROR;
        clear();
        return; // start would go off end of parent
    }
    fCount = count;
  }

private:
  le_uint32 fCount;
};


template<class T>
class LEReferenceTo : public LETableReference {
public:
  /**
   * open a sub reference.
   * @param parent parent reference
   * @param success error status
   * @param atPtr location of reference - if NULL, will be at offset zero (i.e. downcast of parent). Otherwise must be a pointer within parent's bounds.
   */
 inline LEReferenceTo(const LETableReference &parent, LEErrorCode &success, const void* atPtr)
    : LETableReference(parent, parent.ptrToOffset(atPtr, success), LE_UINTPTR_MAX, success) {
    verifyLength(parent.ptrToOffset(atPtr,success), LETableVarSizer<T>::getSize(), success);
    if(LE_FAILURE(success)) clear();
  }
  /**
   * ptr plus offset
   */
 inline LEReferenceTo(const LETableReference &parent, LEErrorCode &success, const void* atPtr, size_t offset)
    : LETableReference(parent, parent.ptrToOffset(atPtr, success)+offset, LE_UINTPTR_MAX, success) {
    verifyLength(0, LETableVarSizer<T>::getSize(), success);
    if(LE_FAILURE(success)) clear();
  }
 inline LEReferenceTo(const LETableReference &parent, LEErrorCode &success, size_t offset)
    : LETableReference(parent, offset, LE_UINTPTR_MAX, success) {
    verifyLength(0, LETableVarSizer<T>::getSize(), success);
    if(LE_FAILURE(success)) clear();
  }
 inline LEReferenceTo(const LETableReference &parent, LEErrorCode &success)
    : LETableReference(parent, 0, LE_UINTPTR_MAX, success) {
    verifyLength(0, LETableVarSizer<T>::getSize(), success);
    if(LE_FAILURE(success)) clear();
  }
 inline LEReferenceTo(const LEFontInstance *font, LETag tableTag, LEErrorCode &success)
   : LETableReference(font, tableTag, success) {
    verifyLength(0, LETableVarSizer<T>::getSize(), success);
    if(LE_FAILURE(success)) clear();
  }
 inline LEReferenceTo(const le_uint8 *data, size_t length = LE_UINTPTR_MAX) : LETableReference(data, length) {}
 inline LEReferenceTo(const T *data, size_t length = LE_UINTPTR_MAX) : LETableReference((const le_uint8*)data, length) {}
 inline LEReferenceTo() : LETableReference(NULL) {}

 inline LEReferenceTo<T>& operator=(const T* other) {
    setRaw(other);
    return *this;
  }

  LEReferenceTo<T> &reparent(const LETableReference &base) {
    fParent = &base;
    return *this;
  }

  /**
   * roll forward by one <T> size.
   * same as addOffset(LETableVarSizer<T>::getSize(),success)
   */
  void addObject(LEErrorCode &success) {
    addOffset(LETableVarSizer<T>::getSize(), success);
  }
  void addObject(size_t count, LEErrorCode &success) {
    addOffset(LETableVarSizer<T>::getSize()*count, success);
  }

  const T *operator->() const { return getAlias(); }
  const T *getAlias() const { return (const T*)fStart; }
  const T *getAliasTODO() const { LE_DEBUG_TR("getAliasTODO<>"); return (const T*)fStart; }
};


U_NAMESPACE_END

#endif

#endif