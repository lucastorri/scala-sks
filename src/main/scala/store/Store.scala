package store

trait Store {

  def get(key: String): Option[String]

  def add(key: String, value: String): Unit

}
