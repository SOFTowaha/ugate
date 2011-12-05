package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;

/**
 * Mail connection GUI responsible for connecting to the mail service
 */
public abstract class MailConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(MailConnectionView.class);
	public static final String LABEL_CONNECT = "Connect To Mail";
	public static final String LABEL_CONNECTING = "Connecting To Mail...";
	public static final String LABEL_RECONNECT = "Reconnect To Mail";
	public final UGateTextFieldPreferenceView smtpHost;
	public final UGateTextFieldPreferenceView smtpPort;
	public final UGateTextFieldPreferenceView imapHost;
	public final UGateTextFieldPreferenceView imapPort;
	public final UGateTextFieldPreferenceView username;
	public final UGateTextFieldPreferenceView password;
	public final Button connect;

	public MailConnectionView() {
	    super(20);
		smtpHost = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_HOST_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, "SMTP Host", "Outgoing email host");
		smtpPort = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, "SMTP Port", "Outgoing email port");
	    imapHost = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, "IMAP Host", "Incoming email host");
		imapPort = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, "IMAP Port", "Incoming email port");
		username = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, "Username", "Username to login with");
		password = new UGateTextFieldPreferenceView(UGateUtil.PV_MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_PASSWORD, "Password", "Password to login with");

		connect = new Button();
	    connectionHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				if (smtpHost.textField.getText().length() > 0 && smtpPort.textField.getText().length() > 0 && 
						imapHost.textField.getText().length() > 0 && imapPort.textField.getText().length() > 0 && 
						username.textField.getText().length() > 0 && password.passwordField.getText().length() > 0) {
					log.debug("Connecting to email...");
					connect(smtpHost.textField.getText(), smtpPort.textField.getText(),
							imapHost.textField.getText(), imapPort.textField.getText(), 
							username.textField.getText(), password.passwordField.getText());
				}
			}
	    };
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, connectionHandler);
	    connect.setText(LABEL_CONNECT);
	    
	    final HBox hostContainer = new HBox(10);
	    hostContainer.getChildren().addAll(smtpHost, smtpPort, imapHost, imapPort);
	    final HBox credContainer = new HBox(10);
	    credContainer.getChildren().addAll(username, password);
	    getChildren().addAll(hostContainer, credContainer, statusIcon, connect);
	}
	
	public void connect(final String smtpHost, final String smtpPort, final String imapHost, 
			final String imapPort, final String username, final String password) {
		connect.setText(LABEL_CONNECTING);
		UGateKeeper.DEFAULT.emailConnect(smtpHost, smtpPort, imapHost, 
				imapPort, username, password, "InBox", true, new IEmailListener(){

					@Override
					public void handle(EmailEvent event) {
						if (event.type == EmailEvent.TYPE_CONNECT) {
							connect.setDisable(false);
							connect.setText(LABEL_RECONNECT);
							log.debug("Turning ON email connection icon");
							setStatusFill(statusIcon, true);
						} else if (event.type == EmailEvent.TYPE_DISCONNECT) {
							connect.setDisable(false);
							connect.setText(LABEL_CONNECT);
							log.debug("Turning OFF email connection icon");
							setStatusFill(statusIcon, false);
						}
					}
		});
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_SMTP_HOST_KEY, smtpHost);
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_SMTP_PORT_KEY, smtpPort);
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_IMAP_HOST_KEY, imapHost);
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_IMAP_PORT_KEY, imapPort);
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_USERNAME_KEY, username);
		UGateKeeper.DEFAULT.preferencesSet(UGateUtil.PV_MAIL_PASSWORD_KEY, password);
	}
	
	public void disconnect() {
		log.info("Disconnecting from Email");
		UGateKeeper.DEFAULT.emailDisconnect();
	}
}
