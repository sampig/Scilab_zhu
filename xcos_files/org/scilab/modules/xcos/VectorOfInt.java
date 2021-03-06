/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.scilab.modules.xcos;

public class VectorOfInt {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VectorOfInt(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VectorOfInt obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        JavaControllerJNI.delete_VectorOfInt(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VectorOfInt() {
    this(JavaControllerJNI.new_VectorOfInt__SWIG_0(), true);
  }

  public VectorOfInt(long n) {
    this(JavaControllerJNI.new_VectorOfInt__SWIG_1(n), true);
  }

  public void ensureCapacity(long n) {
    JavaControllerJNI.VectorOfInt_ensureCapacity(swigCPtr, this, n);
  }

  public void resize(long n) {
    JavaControllerJNI.VectorOfInt_resize(swigCPtr, this, n);
  }

  public int size() {
    return JavaControllerJNI.VectorOfInt_size(swigCPtr, this);
  }

  public boolean isEmpty() {
    return JavaControllerJNI.VectorOfInt_isEmpty(swigCPtr, this);
  }

  public void clear() {
    JavaControllerJNI.VectorOfInt_clear(swigCPtr, this);
  }

  public void add(int x) {
    JavaControllerJNI.VectorOfInt_add__SWIG_0(swigCPtr, this, x);
  }

  public boolean contains(int o) {
    return JavaControllerJNI.VectorOfInt_contains(swigCPtr, this, o);
  }

  public int indexOf(int o) {
    return JavaControllerJNI.VectorOfInt_indexOf(swigCPtr, this, o);
  }

  public int get(int i) {
    return JavaControllerJNI.VectorOfInt_get(swigCPtr, this, i);
  }

  public void set(int i, int val) {
    JavaControllerJNI.VectorOfInt_set(swigCPtr, this, i, val);
  }

  public void add(int i, int val) {
    JavaControllerJNI.VectorOfInt_add__SWIG_1(swigCPtr, this, i, val);
  }

  public boolean remove(int val) {
    return JavaControllerJNI.VectorOfInt_remove(swigCPtr, this, val);
  }

  public java.nio.ByteBuffer asByteBuffer(int i, int capacity) {
    java.nio.ByteBuffer buffer = JavaControllerJNI.VectorOfInt_asByteBuffer(swigCPtr, this, i, capacity);
    buffer.order(java.nio.ByteOrder.nativeOrder());
    return buffer;
  }

}
