import com.typesafe.tools.mima.plugin.MimaPlugin
import sbt._
import sbt.Keys._

object LocalMimaSettings extends AutoPlugin {
  override def requires = MimaPlugin
  override def trigger = allRequirements

  object autoImport {
    lazy val localMimaPreviousVersions = settingKey[Set[String]]("A set of previous versions to compare binary-compatibility against")
  }

  import autoImport._
  import MimaPlugin.autoImport._

  override def projectSettings = Seq[Setting[_]](
    mimaPreviousArtifacts := {
      if (publishArtifact.value)
        localMimaPreviousVersions.value.map {
          projectID
            .value
            .withRevision(_)
            .withExplicitArtifacts(Vector.empty)
        }
      else
        Set.empty
    },
  )
}
