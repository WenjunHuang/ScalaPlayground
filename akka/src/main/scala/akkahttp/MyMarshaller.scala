package akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.{Marshal, Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshaller.EitherUnmarshallingException
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal, Unmarshaller}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

object MyMarshaller extends App {
  implicit val system = ActorSystem("Test")
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  implicit val rawIntFromEntityUnmarshaller:FromEntityUnmarshaller[Int] = {
    Unmarshaller.withMaterializer {
      implicit ex ⇒ implicit mat ⇒ entity:HttpEntity ⇒
        entity.dataBytes
          .runFold(ByteString.empty)(_++_)
          .map(_.utf8String.toInt)
    }
  }

  implicit val rawIntToEntityMarshaller:ToEntityMarshaller[Int] = {
    Marshaller.opaque{value ⇒
      HttpEntity(value.toString)
    }
  }

  implicit def eitherUnmarshaller[L, R](implicit
                                        ua: FromEntityUnmarshaller[L],
                                        rightTag  : ClassTag[L],
                                        ub        : FromEntityUnmarshaller[R],
                                        leftTag   : ClassTag[R]): FromEntityUnmarshaller[Either[L, R]] = {
    Unmarshaller.withMaterializer {
      implicit ex: ExecutionContext ⇒
        implicit mat: Materializer ⇒
          value: HttpEntity ⇒
            import akka.http.scaladsl.util.FastFuture._
            def right = ub(value).fast.map(Right(_))

            def fallbackLeft: PartialFunction[Throwable, Future[Either[L, R]]] = {
              case rightFirstEx ⇒
                val left = ua(value).fast.map(Left(_))
                left.transform(
                  s = identity,
                  f = leftSecondEx ⇒ new EitherUnmarshallingException(
                    rightClass = rightTag.runtimeClass,
                    right = rightFirstEx,
                    leftClass = leftTag.runtimeClass,
                    left = leftSecondEx
                  )
                )
            }

            right.recoverWith(fallbackLeft)

    }
  }

  implicit def eitherMarshaller[L,R](implicit
                                     ua:ToEntityMarshaller[L],
                                     ub:ToEntityMarshaller[R]):ToEntityMarshaller[Either[L,R]] = {
    Marshaller {context ⇒value ⇒
      if (value.isRight)
        ub(value.right.get)
      else
        ua(value.left.get)
    }
  }


  val testLeft = Unmarshal(HttpEntity("42")).to[Either[String, Int]]
  val mar = Marshal(Left("42")).to[HttpEntity]


}
