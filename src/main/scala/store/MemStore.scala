package store

import scala.collection.mutable

class MemStore extends Store {

  private val store = mutable.HashMap.empty[String, String]

  override def get(key: String): Option[String] = store.get(key)

  override def add(key: String, value: String): Unit = store(key) = value

}
