package macwire
import com.softwaremill.macwire._

class FacebookAccess(userFind:UserFinder)
class UserFinder()

@Module
class UserModule {
  lazy val userFinder = wire[UserFinder]
}

class SocialModule(userModule:UserModule) {
  lazy val facebookAccess = wire[FacebookAccess]
}

object ComposingModules extends App{
  val userModule = new UserModule
  val socialModule = new SocialModule(userModule)

  socialModule.facebookAccess
}
