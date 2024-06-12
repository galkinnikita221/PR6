package com.example.singin_galkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public int start_x = 0;
    private int startY;
    private int currentPage;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int endY = (int) event.getY();
                if (startY - endY > 50) { // Свайп вверх
                    goToNextPage();
                } else if (endY - startY > 50) { // Свайп вниз
                    goToPreviousPage();
                }
                break;
        }
        return true;
    }

    private void goToNextPage() {
        switch (currentPage) {
            case 0:
                setContentView(R.layout.regin);
                currentPage = 1;
                break;
            case 1:
                setContentView(R.layout.password);
                currentPage = 2;
                break;
        }
    }

    private void goToPreviousPage() {
        switch (currentPage) {
            case 1:
                setContentView(R.layout.activity_main);
                currentPage = 0;
                break;
            case 2:
                setContentView(R.layout.regin);
                currentPage = 1;
                break;
        }
    }

    public void onPassword(View view) {
        setContentView(R.layout.password);
    }

    public void onStart(View view) {
        setContentView(R.layout.activity_main);
    }

    public String login, password;
    public void onAuthorization(View view) {
        TextView tv_login = findViewById(R.id.login);
        String login = tv_login.getText().toString();

        TextView tv_password = findViewById(R.id.password);
        String password = tv_password.getText().toString();

        GetDataUser gdu = new GetDataUser(login, password);
        gdu.execute();
    }



    public class DataUser{
        public String id;
        public String login;
        public String password;

        public void setId(String _id) {this.id = _id;}
        public String getId(){return this.id;}
        public void setLogin(String _login) {this.login = _login;}
        public String getLogin(){
            return this.login;}
        public void setPassword(String _password) {this.password = _password;}
        public String getPassword(){return this.password;}
    }
    ArrayList<DataUser> dataUser = new ArrayList();

    class GetDataUser extends AsyncTask<Void, Void, String> {
        String login;
        String password;

        public GetDataUser(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            String body = null;
            try {
                Log.d("GetDataUser", "Connecting to server...");
                Document doc_b = Jsoup.connect("http://192.168.1.54/index.php?login=" + login + "&password=" + password).get();
                body = doc_b.text();
                Log.d("GetDataUser", "Response body: " + body);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("GetDataUser", "Error connecting to server", e);
            }
            return body;
        }

        @Override
        protected void onPostExecute(String body) {
            super.onPostExecute(body);
            if (body == null || body.isEmpty()) {
                AlertDialog("Ошибка", "Не удалось подключиться к серверу.");
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(body);
                dataUser.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonRead = jsonArray.getJSONObject(i);
                    DataUser duUser = new DataUser();
                    duUser.setId(jsonRead.getString("id"));
                    duUser.setLogin(jsonRead.getString("login"));
                    duUser.setPassword(jsonRead.getString("password"));
                    dataUser.add(duUser);
                }
                if (dataUser.size() != 0) {
                    AlertDialog("Авторизация", "Пользователь авторизован.");
                } else {
                    AlertDialog("Авторизация", "Пользователя с таким логином или паролем не существует.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                AlertDialog("Ошибка", "Ошибка обработки данных.");
            }
        }
    }

    public void onRegistration(View view) {
        // Генерация капчи
        String captcha = generateRandomCaptcha(6);
        // Сохранение капчи в переменной
        String actualCaptcha = captcha;

        // Установка капчи в TextView
        TextView captchaTextView = findViewById(R.id.captchaTextView);
        captchaTextView.setText(captcha);

        // Получение значений логина, пароля и введенной капчи
        TextView tv_login = findViewById(R.id.login2);
        TextView tv_password = findViewById(R.id.password2);
        TextView tv_password2 = findViewById(R.id.reapeatPassword);
        EditText et_captcha = findViewById(R.id.captcha);

        String a = tv_password.getText().toString();
        String b = tv_password2.getText().toString();
        String enteredCaptcha = et_captcha.getText().toString();

        // Сравнение паролей и капч
        if (a.equals(b)) {
            if (enteredCaptcha.equalsIgnoreCase(actualCaptcha)) {
                // Если все совпадает, выполнить регистрацию
                login = tv_login.getText().toString();
                password = tv_password.getText().toString();

                SetDataUser sdu = new SetDataUser();
                sdu.execute();
            } else {
                // Если капча не совпадает, вывести сообщение об ошибке
                AlertDialog("Авторизация", "Неверная капча");
            }
        } else {
            // Если пароли не совпадают, вывести сообщение об ошибке
            AlertDialog("Авторизация", "Пароли не совпадают");
        }
    }
    private String captcha = null;

    private String generateRandomCaptcha(int length) {
        if (captcha == null) {
            String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder generatedCaptcha = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                generatedCaptcha.append(symbols.charAt(random.nextInt(symbols.length())));
            }
            captcha = generatedCaptcha.toString();
        }
        return captcha;
    }


    class SetDataUser extends AsyncTask<Void, Void, String> {
        String body;

        @Override
        protected String doInBackground(Void... params) {
            Document doc_b = null;
            try {
                Log.d("SetDataUser", "Connecting to server...");
                doc_b = Jsoup.connect("http://192.168.1.54/regin.php?login=" + login + "&password=" + password).get();
                body = doc_b.text();
                Log.d("SetDataUser", "Response body: " + body);
            } catch(IOException e) {
                e.printStackTrace();
                Log.e("SetDataUser", "Error connecting to server", e);
            }
            if(doc_b != null) {
                body = doc_b.text();
            } else {
                body = "Ошибка!";
            }
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(body.length() != 0) {
                if(body.contains("0")) {
                    AlertDialog("Авторизация", "Пользователь с таким логином существует.");
                } else if(body.contains("1")) {
                    AlertDialog("Авторизация", "Пользователь зарегистрирован.");
                }
            } else {
                AlertDialog("Авторизация", "Ошибка данных.");
            }
        }
    }
    public void AlertDialog(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public  void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}