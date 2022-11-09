import cats.effect.{IO, IOApp}
import scala.concurrent.duration._
import cats.Monad
import cats.implicits._
import cats.effect.ExitCode
import cats.Id

final case class FooId(value: Int) extends AnyVal

final case class Foo(
                      id: FooId,
                      references: List[FooId],
                      data: String
                    )

trait FooRepository[F[_]] {
  def read(ids: List[FooId]): F[Set[Foo]]
}

case class Repository(list: List[Foo]) extends FooRepository[Id] {
  override def read(ids: List[FooId]): Id[Set[Foo]] =
    Monad[Id].pure(ids.flatMap(id => list.filter(x => x.id == id)).toSet)
}

object read {
  def readClosure[F[_] : Monad](
                                 repo: FooRepository[F], ids: List[FooId]): F[Set[Foo]] = {
    val acc: Set[Foo] = Set.empty
    Monad[F].tailRecM((acc, ids.toSet)) {
      case (acc, current) => current.toList match {
        case Nil => Monad[F].pure(Right(acc))
        case current =>
          repo.read(current.toList).map {
            xs =>
              val nextAcc = acc ++ xs
              Left(nextAcc, xs.flatMap(_.references.diff(nextAcc.map(x => x.id).toSeq)))
          }
      }
    }
  }
}

object Main extends App {

  val records = List(
    Foo(FooId(1), List(FooId(3), FooId(5)), "One"),
    Foo(FooId(2), List(FooId(4), FooId(6)), "Two"),
    Foo(FooId(3), List(FooId(1), FooId(5), FooId(7)), "Three"),
    Foo(FooId(4), List(FooId(2), FooId(6)), "Four"),
    Foo(FooId(5), List(FooId(1), FooId(3)), "Five"),
    Foo(FooId(6), List(FooId(2), FooId(4)), "Six"),
    Foo(FooId(7), List(), "Seven")
  )

  val recordsShort = List(
    Foo(FooId(1), List(FooId(2)), "One"),
    Foo(FooId(2), List(FooId(4), FooId(6)), "Two"),
    Foo(FooId(4), List(), "Four"),
    Foo(FooId(6), List(), "Six"),
  )
  val repo = Repository(records)
  print(read.readClosure[Id](repo, List(FooId(1))).mkString("\n"))


}