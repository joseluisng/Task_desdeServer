package com.joseluisng.task_desdeserver

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.task_molde.view.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var listaTask = ArrayList<Task>()
    //private lateinit var adapter: AdapterTask
    private lateinit var adapter: AdaptadorTask

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable
    private lateinit var mRandom: Random

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener {
            val intent = Intent(this, ActivityAdd::class.java )
            startActivity(intent)
        }

        adapter = AdaptadorTask(this, listaTask)

        cargarTask()

        mHandler = Handler()
        // Refrescar la lista deslizando hacia abajo por si se realizaron cambios
        swipe_refresh_layout.setOnRefreshListener {
            mRunnable = Runnable {
                cargarTask()

                swipe_refresh_layout.isRefreshing = false
            }

            mHandler.postDelayed(mRunnable, 2000)
        }


    }

    override fun onResume() {
        super.onResume()

        /*
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "On Resumen", Toast.LENGTH_SHORT).show()
        Log.e("Adaptador", adapter.notifyDataSetChanged().toString())
        cargarTask()
        */

    }


    fun cargarTask(){

        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.103:3000/api/tasks"

        val jsonArrayRequest = JsonArrayRequest(url,
                Response.Listener{response ->
                    Log.i("Mensaje: ", "Response is $response")
                    Log.e("Mensaje: ", "Response is $response")
                    var x = 0
                    listaTask.clear()
                    while(x < response.length()){

                        val jsonObject = response.getJSONObject(x)

                        listaTask.add(Task(jsonObject.getString("_id"), jsonObject.getString("title"), jsonObject.getString("description")))

                        x++
                    }

                    //adapter = AdaptadorTask(this, listaTask)
                    listViewTask.adapter = adapter

                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
        )

        queue.add(jsonArrayRequest)

    }


    inner class AdaptadorTask(context: Context, var listaTask: List<Task>): BaseAdapter(){

        var context: Context? = context

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
                val url: String = "http://192.168.0.102:3000/api/tasks/${selectArgs}"


                val req = JsonObjectRequest(Request.Method.DELETE, url, null,
                        Response.Listener { response ->
                            Log.e("Mensaje", "response is: $response")

                            Toast.makeText(context, "Se borro", Toast.LENGTH_SHORT).show()
                            cargarTask()
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
            return listaTask[position]        }

        override fun getItemId(position: Int): Long {
            return position.toLong()        }

        override fun getCount(): Int {
            return listaTask.size
        }

    } // Finaliza la clase del Adaptador


}
