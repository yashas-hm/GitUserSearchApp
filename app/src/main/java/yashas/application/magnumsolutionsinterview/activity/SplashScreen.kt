package yashas.application.magnumsolutionsinterview.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import yashas.application.magnumsolutionsinterview.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val startAct = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(startAct)
            finish()
        }, 1500)
    }
}