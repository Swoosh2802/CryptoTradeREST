package CryptoApiRest.Model;

import org.hibernate.validator.constraints.Length;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user")
public class User {
    final static Logger logger = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUser")
    private long idUser;

    @NotNull
    @Column(name = "pseudo")
    private String pseudo;

    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name="oAuthCookie")
    private String oAuthCookie;

    @Column(name="password")
    private String password;

    @NotNull
    @Column(name="isBlocked")
    private Boolean isBlocked;

    @Column(name="notification")
    private String notification;

    @Column(name="notification_web")
    private String notification_web;

    @Length(min = 0, max = 1000)
    @Column(name="token")
    private String token;

    public User() {
    }

    public User(final long userId) {
        super();
        this.idUser = userId;
    }

    public User(final String email, final String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public User(final String pseudo,final String email, final String password, final String oAuthCookie) {
        super();
        this.pseudo = pseudo;
        this.oAuthCookie = oAuthCookie;
        this.email = email;
        this.password = password;
        this.isBlocked = false;
        this.notification = null;
        this.notification_web = null;
        this.token = null;
    }

    public long getId() {
        return idUser;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getEmail() {
        return email;
    }

    public String getoAuthCookie(){
        return oAuthCookie;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public String getNotification() {
        return notification;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setoAuthCookie(String oAuthCookie) {
        this.oAuthCookie = oAuthCookie;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String hashedPassword){
        password = hashedPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNotificationWeb() {
        return notification_web;
    }

    public void setNotificationWeb(String notification_web) {
        this.notification_web = notification_web;
    }

    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("idUser", idUser);
            jsonObject.put("pseudo", pseudo);
            jsonObject.put("email", email);
            jsonObject.put("isBlocked", isBlocked);
            jsonObject.put("oAuthCookie", oAuthCookie);
            jsonObject.put("notification", notification);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        return jsonObject;
    }

}