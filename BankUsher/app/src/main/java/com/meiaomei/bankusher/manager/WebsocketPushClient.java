package com.meiaomei.bankusher.manager;

import android.os.Handler;
import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class WebsocketPushClient extends WebSocketClient {
    private static final String TAG = "WebsocketPushClient";


    public interface Callback {
        public void OnReciveData(String data);
    }

    public void setCallback(Callback cb) {
        this._callback = cb;
    }

    protected Callback _callback = null;

    public WebsocketPushClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketPushClient(URI serverURI) {
        super(serverURI);
    }

    protected String m_sOrgCode = "";

    public void setOrgCode(String s) {
        m_sOrgCode = s;
    }

    protected  String m_sOranization="";

    public void setM_sOranization(String m_sOranization) {
        this.m_sOranization = m_sOranization;
    }

    protected CallErroBack callErroBack;
    public interface CallErroBack {
        public void OnReciveError();
    }

    public void setCallErroBack(CallErroBack callErroBack) {
        this.callErroBack = callErroBack;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {//打开连接 发送消息走着
        Log.e(TAG, "onOpen: Websocket opened connection===" + Thread.currentThread().getId());
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
        JSONObject object = new JSONObject();
        try {
            object.put("orgCode", m_sOrgCode);
            object.put("organizationCode",m_sOranization);
            this.send("" + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        if (null != _callback) {
            _callback.OnReciveData(message);
        }

        Log.e(TAG, "onMessage Websocket received: " + message);
    }

//    @Override
//    public void onFragment(Framedata fragment) {
//        System.out.println("Websocket received fragment: " + new String(fragment.getPayloadData().array()));
//    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, "onClose-------  " + reason);
        this.close();
        if (null!=callErroBack){
            callErroBack.OnReciveError();
        }

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        System.out.println("Websocket onError " + ex.getMessage());
        Log.e(TAG, "onError---------- " + ex.getLocalizedMessage());
        this.close();
    }

	/*public static void main( String[] args ) throws URISyntaxException {
        WebsocketPushClient client = new WebsocketPushClient( new URI( "ws://192.168.50.133:8080/falcon-web/webSocketServer" ), new Draft_6455() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts

		long ticketCon = 0;
		while(true) {

			if (System.currentTimeMillis() - ticketCon > 3000) {
				ticketCon = System.currentTimeMillis();

				if (! client.isOpen())
					client.connect();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//System.out.println("main end");
	}*/

}