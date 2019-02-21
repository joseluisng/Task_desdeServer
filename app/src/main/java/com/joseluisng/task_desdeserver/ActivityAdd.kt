package com.joseluisng.task_desdeserver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_add.*
import org.json.JSONObject

class ActivityAdd : AppCompatActivity() {

    var id = "0"
    val LOG_TAG: String = "Mensaje"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val etTituloTask = findViewById<EditText>(R.id.editTituloTask) as EditText
        val etDescTask = findViewById<EditText>(R.id.editDescripcionTask) as EditText
        val btn_agregar = findViewById<Button>(R.id.btn_agregar) as Button

        try{
            val bundle: Bundle = intent.extras!!
            id = bundle.getString("ID", "0")
            if(id != "0"){
                etTituloTask.setText(bundle.getString("titulo"))
                etDescTask.setText(bundle.getString("descrip"))
            }
        }catch (e: Exception){

        }

        btn_agregar.setOnClickListener {

            val tituloTask = etTituloTask.text.toString()
            val descTask = etDescTask.text.toString()

            if(!tituloTask.isEmpty() && !descTask.isEmpty()){

                if(id == "0"){
                    btn_agregar(tituloTask, descTask)
                }else{
                    actualizarTask(tituloTask, descTask)
                }

            }else{
                Toast.makeText(this, "Llenar los campos Titulo y Descripción", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun btn_agregar(titulo: String, description: String){

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://192.168.0.103:3000/api/tasks"

        val jsonObject = JSONObject()

        jsonObject.put("title", titulo)
        jsonObject.put("description", description)

        val req = JsonObjectRequest(url, jsonObject,
                Response.Listener { response ->
                    Log.i(LOG_TAG, "response is: $response")
                    Log.e(LOG_TAG, "response is: $response")
                    intent = Intent(this, MainActivity::class.java )
                    Toast.makeText(this, "Se agrego correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    Toast.makeText(this, "No se pudo agregar", Toast.LENGTH_SHORT).show()
                }
        )

        queue.add(req)
    }

    fun actualizarTask(titulo: String, description: String){

        val queue = Volley.newRequestQueue(this)
        val url: String = "http://192.168.0.103:3000/api/tasks/${id}"

        val jsonObject = JSONObject()

        jsonObject.put("title", titulo)
        jsonObject.put("description", description)

        val req = JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                Response.Listener { response ->
                    Log.i(LOG_TAG, "response is: $response")
                    Log.e(LOG_TAG, "response is: $response")

                    intent = Intent(this, MainActivity::class.java )
                    Toast.makeText(this, "Se actualizo", Toast.LENGTH_SHORT).show()
                    startActivity(intent)

                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                }
        )

        queue.add(req)

    }
}
