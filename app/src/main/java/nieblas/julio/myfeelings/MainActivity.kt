package nieblas.julio.myfeelings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.widget.Toast
import nieblas.julio.myfeelings.utilities.CustomBarDrawable
import nieblas.julio.myfeelings.utilities.CustomCircleDrawable
import nieblas.julio.myfeelings.utilities.JSONFile
import nieblas.julio.myfeelings.utilities.Emociones
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var jsonFile: JSONFile?=null
    var veryHappy=0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verysad = 0.0F
    var data: Boolean=false
    var lista= ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jsonFile= JSONFile()
        fetchingData()
        if(!data){
            var emociones=ArrayList<Emociones>()
            val fondo= CustomCircleDrawable(this,emociones)
            graph.background=fondo
            graphVeryHappy.background= CustomBarDrawable(this,Emociones("Muy Feliz",0.0F,R.color.mustard,veryHappy))
            graphHappy.background=CustomBarDrawable(this,Emociones("Feliz",0.0F,R.color.orange,happy))
            graphNeutral.background=CustomBarDrawable(this,Emociones("Neutral",0.0F,R.color.greenie,neutral))
            graphSad.background=CustomBarDrawable(this,Emociones("Triste",0.0F,R.color.blue,sad))
            graphVerySad.background=CustomBarDrawable(this,Emociones("Muy Triste",0.0F,R.color.deepBlue,verysad))


        }else{
            actualizarGrafica()
            iconMayoria()

        }

        guardarButton.setOnClickListener {
            guardar()
        }

        veryHappyButton.setOnClickListener {
            veryHappy++
            iconMayoria()
            actualizarGrafica()
        }

        happyButton.setOnClickListener {
            happy++
            iconMayoria()
            actualizarGrafica()
        }

        neutralButton.setOnClickListener {
            neutral++
            iconMayoria()
            actualizarGrafica()
        }

        sadButton.setOnClickListener {
            sad++
            iconMayoria()
            actualizarGrafica()
        }
        verySadButton.setOnClickListener {
            verysad++
            iconMayoria()
            actualizarGrafica()
        }


    }

    fun fetchingData(){
        try{
            var json:String = jsonFile?.getData(this)?:""
            if(json!=""){
                this.data=true
                var jsonArray:JSONArray= JSONArray(json)
                this.lista = parseJson(jsonArray)

                for(i in lista){
                    when(i.nombre){
                        "Muy Feliz" -> veryHappy= i.total
                        "Feliz" ->happy=i.total
                        "Neutral" -> neutral=i.total
                        "Triste" -> sad = i.total
                        "Muy Triste" -> verysad=i.total
                    }
                }
            }else{
                this.data=false
            }
        }catch (excep:JSONException){
            excep.printStackTrace()
        }


    }

    fun iconMayoria(){
        if(happy>veryHappy && happy>neutral && happy>sad && happy>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        }
        if(veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        }
        if(neutral>veryHappy && neutral>happy && neutral>sad && neutral>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        }
        if(sad>veryHappy && sad>neutral && sad>happy && sad>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        }
        if(verysad>veryHappy && verysad>neutral && verysad>sad && verysad>happy){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }


    }

    fun actualizarGrafica(){
        var total = veryHappy+happy+neutral+verysad+sad

        var pVH: Float= (veryHappy*100/total).toFloat()
        var pH: Float=(happy*100/total).toFloat()
        var pN:Float=(neutral*100/total).toFloat()
        var pS:Float=(sad*100/total).toFloat()
        var pVS: Float=(verysad*100/total).toFloat()

        Log.d("porcentajes","very happy "+pVH)
        Log.d("porcentajes","happy "+pH)
        Log.d("porcentajes","neutral "+pN)
        Log.d("porcentajes","sad "+pS)
        Log.d("porcentajes","very sad "+pVS)

        lista.clear()
        lista.add(Emociones("Muy Feliz",pVH,R.color.mustard,veryHappy))
        lista.add(Emociones("Feliz",pH,R.color.orange,happy))
        lista.add(Emociones("Neutral",pN,R.color.greenie,neutral))
        lista.add(Emociones("Triste",pS,R.color.blue,sad))
        lista.add(Emociones("Muy Triste",pVS,R.color.deepBlue,verysad))

        val fondo= CustomCircleDrawable(this,lista)

        graphVeryHappy.background= CustomBarDrawable(this,Emociones("Muy Feliz",pVH,R.color.mustard,veryHappy))
        graphHappy.background=CustomBarDrawable(this,Emociones("Feliz",pH,R.color.orange,happy))
        graphNeutral.background=CustomBarDrawable(this,Emociones("Neutral",pN,R.color.greenie,neutral))
        graphSad.background=CustomBarDrawable(this,Emociones("Triste",pS,R.color.blue,sad))
        graphVerySad.background=CustomBarDrawable(this,Emociones("Muy Triste",pVS,R.color.deepBlue,verysad))

        graph.background=fondo

    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones>{
        var lista= ArrayList<Emociones>()

        for (i in 0..jsonArray.length()){
            try{
                val nombre= jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color= jsonArray.getJSONObject(i).getInt("color")
                val total= jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre,porcentaje,color,total)
                lista.add(emocion)

            }catch (excep: JSONException){
                excep.printStackTrace()
            }

        }

        return lista
    }

    fun guardar(){
        var jsonArray=JSONArray()
        var o:Int = 0

        for(i in lista){
            Log.d("objetos", i.toString())
            var j: JSONObject = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color",i.color)
            j.put("total",i.total)

            jsonArray.put(o,j)
            o++

        }
        jsonFile?.saveData(this,jsonArray.toString())

        Toast.makeText(this,"Datos Guardados",Toast.LENGTH_SHORT).show()


    }
}
