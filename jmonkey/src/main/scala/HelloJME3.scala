import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box

object HelloJME3 extends SimpleApplication {
  override def simpleInitApp(): Unit = {
    val b = new Box(1,1,1)
    val geom = new Geometry("Box",b)
    val mat = new Material(assetManager,
                           "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color",ColorRGBA.Blue)
    geom.setMaterial(mat)
    rootNode.attachChild(geom)
  }

  def main(args: Array[String]): Unit = {
    start()
  }
}
