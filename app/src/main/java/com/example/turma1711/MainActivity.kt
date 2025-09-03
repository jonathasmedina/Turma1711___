package com.example.turma1711

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var bt: Button
    private lateinit var btCriarUsuario: Button
    private lateinit var btEqueciSenha: Button
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt = findViewById(R.id.buttonLogar)
        btCriarUsuario = findViewById(R.id.buttonCriar)
        btEqueciSenha = findViewById(R.id.buttonResetSenha)

        mAuth = Firebase.auth


        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = mAuth.currentUser
            if(user != null){
                if(user.isEmailVerified){
                    Toast.makeText(this, "Bem vindo ${user.email}", Toast.LENGTH_SHORT).show()
                   // startActivity(Intent(this, TelaLogado::class.java))
                   // finish()
                }
                else{
                    Toast.makeText(this, "Verifique seu email", Toast.LENGTH_SHORT).show()
                    mAuth.signOut()
                }
            }
            else{
                //usuario nao logado
                //muda pra tela de login, se necessário
                finish()
            }
        }

        btEqueciSenha.setOnClickListener {
            mAuth.sendPasswordResetEmail("jonathas.medina@ifms.edu.br")
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Email enviado", Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btCriarUsuario.setOnClickListener {
            mAuth.createUserWithEmailAndPassword(
                "jonathas.medina@ifms.edu.br",
                "123456"
            ).
            addOnCompleteListener{
                if(it.isSuccessful()){
                    val user = mAuth.currentUser
                    user?.sendEmailVerification()
                    //usuario criado
                    Toast.makeText(this, "Usuario criado", Toast.LENGTH_SHORT).show()
                }
                else{
                    //usuario nao foi criado
                    Toast.makeText(this, "Erro: Usuario não criado", Toast.LENGTH_SHORT).show()

                }
            }
        }

        bt.setOnClickListener {
            mAuth.signInWithEmailAndPassword(
                "jonathas.medina@ifms.edu.br",
                "123456").
            addOnCompleteListener {
                   if(it.isSuccessful()){
                        //loguei
                       val user = mAuth.currentUser

                       if(user != null && user.isEmailVerified){
                           startActivity(Intent(this, TelaLogado::class.java))
                       }
                       else{
                           mAuth.signOut()
                           Toast.makeText(this, "Verifique sua conta", Toast.LENGTH_SHORT).show()
                       }

                       Log.d("usuario", ""+user)
                    }
                else{
                    //algum erro
                       Toast.makeText(this, "Erro ao logar", Toast.LENGTH_SHORT).show()
                   }
            }
        }
    }

    /*
    adicionar e remover o listener para verificar estado do login
    boa prática:
    - onStart e onStop
    - não deixar na onCreate pois não sai da memória, até crashar
     */

    //onStart = activity prestes a se tornar visível ao usuário
    //tela sem foco, não interativa ainda
    //é também executado após o onCreate
    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener { mAuthListener }
    }

    //tela deixa de estar visível ao usuário.
    // Ex: outra tela acima/app minimizado/app foi pro background
    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener { mAuthListener }
    }

}