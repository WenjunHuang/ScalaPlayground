package scala_lang.nameparam

import scala.collection.mutable

trait Observable {
  type Handle <: {
    def remove(): Unit
  }

  val callbacks: mutable.Map[Handle, this.type ⇒ Unit] = mutable.Map()

  def createHandle(function: (Observable.this.type) ⇒ Unit): Handle

  def observe(callback: this.type ⇒ Unit): Handle = {
    val handle = createHandle(callback)
    callbacks += (handle → callback)
    handle
  }

  def unobserve(handle: Handle): Unit = {
    callbacks -= handle
  }

  protected def notifyListeners(): Unit =
    for (callback ← callbacks.values) callback(this)
}

class VariableStore[T](var value: T) extends Observable {
  override type Handle = RemoveableHandle

  override def createHandle(function: (VariableStore.this.type) ⇒ Unit): Handle = {
    new RemoveableHandle
  }

  def set(newValue: T): Unit = {
    value = newValue
    notifyListeners()
  }

  class RemoveableHandle {
    def remove(): Unit = {
      VariableStore.this.unobserve(this)
    }
  }

}

trait Dependencies {
  type Ref = x.Handle forSome {val x: Observable}

  var handles = List[Ref]()

  def addHandle(handle: Ref): Unit = {
    handles :+= handle
  }

  def removeDependencies(): Unit = {
    for (h ← handles) h.remove()
    handles = List()
  }

  def observe[T <: Observable](obj: T)(handler: T ⇒ Unit): Ref = {
    val ref = obj.observe(handler)
    addHandle(ref)
    ref
  }
}
