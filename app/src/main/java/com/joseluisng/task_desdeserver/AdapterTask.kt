package com.joseluisng.task_desdeserver

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.task_molde.view.*
import org.json.JSONObject

//Al declarar la clase ya pasamos el contructor entre parentesis
class AdapterTask( context: Context, var listaTask: List<Task>): BaseAdapter(){

    var context: Context? = context
    val LOG_TAG:String = "Mensaje"


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //Hacemos un reciclado de vista
        var convertView: View? = convertView
        if(convertView == null){
            convertView = View.inflate(context, R.layout.task_molde, null)
        }

        val task = listaTask[position]

        val miVista = convertView!!
        miVista.tvTitleTask.text = task.title
        miVista.tvDecTask.text = task.description

        miVista.imageViewBorrar.setOnClickListener {

            val selectArgs = task.id

            val queue = Volley.newRequestQueue(context)
            val url: String = "http://192.168.0.103:3000/api/tasks/${selectArgs}"


            val req = JsonObjectRequest(Request.Method.DELETE, url, null,
                    Response.Listener { response ->
                        Log.i(LOG_TAG, "response is: $response")
                        Log.e(LOG_TAG, "response is: $response")

                        Toast.makeText(context, "Se borro", Toast.LENGTH_SHORT).show()

                    },
                    Response.ErrorListener { error ->
                        error.printStackTrace()
                        Toast.makeText(context, "No se pudo Borrar", Toast.LENGTH_SHORT).show()
                    }
            )

            queue.add(req)

        }

        miVista.imageViewEditar.setOnClickListener {

            val selectArgs = task.id

            //context!!.startActivity(Intent(context, ActivityAdd::class.java))

            val intent = Intent(context, ActivityAdd::class.java)
            intent.putExtra("ID", selectArgs)
            intent.putExtra("titulo", task.title)
            intent.putExtra("descrip", task.description)
            context!!.startActivity(intent)
        }

        return miVista
    }

    override fun getItem(position: Int): Any {
        return listaTask[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listaTask.size
    }

}