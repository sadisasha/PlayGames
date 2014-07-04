Для корректной работы плагина нужно в файле MainActivity (или HelloworldActivity везде имя разное) проекта, дописать два метода.

По умалчиванию код:

public class GPGTest extends CordovaActivity
{
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {    	
    	super.onCreate(savedInstanceState);
       	super.init();
        // Set by <content src="index.html" /> in config.xml
        super.loadUrl(Config.getStartUrl());
        //super.loadUrl("file:///android_asset/www/index.html");
    }
}

Нужно после метода onCreate вставить вот этот код

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
        	case PlayGamesHelper.REQUEST_CODE_RESOLUTION: {
		PlayGamesServices.reconnect();
        	}
            	break;
        }
	}
	
    

	@Override
    public void onDestroy() {
    	PlayGamesServices.disconnect();
        super.onDestroy();
    }

И добавить импорт
import com.anyks.playgames.playgamesutils.PlayGamesHelper;
import com.anyks.playgames.playgamesutils.PlayGamesServices;
import android.content.Intent;

Иначе при первом запуске программы коннект с сервисом произойдет, но всплывающее окно «Добро пожаловать» не появится, оно появится при следующем запуске приложения

P.S: небольшой косяк, при первом запуске окно всплывает при коннекте, при следующих запусках коннект происходит, но окно «Добро пожаловать» не всплывет. Постараюсь вскоре исправить.

P.S.S: инструкция по работе методов в комментариях, в playgames.js

P.S.S: в папке res в файле ids.xml нужно вставить свои идентификаторы из google консоли