package CryptoApiRest.Controller;

import CryptoApiRest.Model.Crypto;
import CryptoApiRest.Model.User;
import CryptoApiRest.Model.Wallet;
import CryptoApiRest.Repositories.UserRepository;
import CryptoApiRest.Exception.*;
import CryptoApiRest.Repositories.WalletRepository;
import CryptoApiRest.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static CryptoApiRest.Tools.getResponse;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Bean
    final PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final PasswordEncoder passwordEncoder = passwordEncoder();

    // get user by id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User login(@RequestBody User user){
        User userGet = userRepository.findByEmail(user.getEmail());
        if(userGet != null && passwordEncoder.matches(user.getPassword(), userGet.getPassword())){
            String token = getJWTToken(user.getPseudo());
            userGet.setToken(token);
            userRepository.save(userGet);
            return userGet;
        }
        return null;
    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();
        return "Bearer " + token;
    }

    @RequestMapping(value = "/isConnected", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User loginByToken(@RequestBody String objectString){
        try {
            final JSONObject jsonObject = new JSONObject(objectString);

            final User user = userRepository.findByToken(jsonObject.getString("token"));

            if(user == null){
                return null;
            }

            user.setToken(getJWTToken(user.getPseudo()));
            userRepository.save(user);

            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/setNotification/{id}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String setNotification(@PathVariable(value = "id") long userId, @RequestBody String jsonString){
        final User user = userRepository.findByIdUser(userId);

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.has("forWeb")){
                if(jsonObject.has("tokenUser")){
                    user.setNotificationWeb(jsonObject.getString("tokenUser"));
                }else{
                    user.setNotificationWeb(null);
                }
            }else{
                if(jsonObject.has("tokenUser")){
                    user.setNotification(jsonObject.getString("tokenUser"));
                }else{
                    user.setNotification(null);
                }
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        userRepository.save(user);

        return "{\"code\":\"Ok\"}";
    }

    // insert user
    @RequestMapping(value = "/insertUser", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String createUser(@RequestBody User user) {
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message", "ERROR");

            final Matcher matcher = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(user.getEmail());

            if(user.getPseudo().equals("")){
                jsonObject.put("pseudo", "Le pseudo ne peut pas être vide.");
            }else if(userRepository.findByPseudo(user.getPseudo()) != null){
                jsonObject.put("pseudo", "Le pseudo existe déjà.");
            }else if(!matcher.find()){
                jsonObject.put("email", "L'email n'a pas un bon format.");
            }else if(userRepository.findByEmail(user.getEmail()) != null){
                jsonObject.put("email", "L'email existe déjà.");
            }else if(user.getPassword().equals("")){
                jsonObject.put("password", "Le mot de passe ne peut pas être vide.");
            }else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setBlocked(false);
                user.setNotification(null);
                user.setoAuthCookie(null);
                final User newUser = userRepository.save(user);
                addMoneyToNewAccount(newUser);

                jsonObject.put("message", "OK");
                jsonObject.put("user", newUser.toJSON());
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        return jsonObject.toString();
    }

    @RequestMapping(value = "/loginWithGmail", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User loginWithGmail(@RequestBody User user) {
        final User userRequest = userRepository.findByoAuthCookie(user.getoAuthCookie());
        if(userRequest == null){
            user.setPseudo(user.getPseudo());
            user.setEmail(user.getEmail());
            user.setBlocked(false);
            user.setNotification(null);
            String token = getJWTToken(user.getPseudo());
            System.out.println(token);
            System.out.println(user.getId());
            user.setToken(token);
            final User newUser = userRepository.save(user);
            addMoneyToNewAccount(newUser);
            return newUser;
        }else{
            return userRequest;
        }
    }

    private void addMoneyToNewAccount(final User user){
        final String response = getResponse("GET", Tools.getUrlCrypto() +"/data/pricemulti?api_key="+ Tools.getApiKeyCrypto() +"&fsyms=EUR&tsyms=USDC");
        try {
            final JSONObject objectEur = new JSONObject(response);
            final JSONObject objectUSD = new JSONObject(objectEur.getString("EUR"));
            final double USDCPriceUnit = objectUSD.getDouble("USDC");
            walletRepository.save(new Wallet((USDCPriceUnit * 100), user, new Crypto("USDC")));
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }
    }

    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAccount(@RequestBody String user) {
        final JSONObject jsonObject = new JSONObject();

        try {
            final JSONObject jsonUser = new JSONObject(user);
            final User userRequest = userRepository.findByIdUser(jsonUser.getLong("idUser"));

            try {
                jsonObject.put("message", "ERROR");

                if(userRequest != null){
                    final Matcher matcher = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(jsonUser.getString("email"));

                    if(!matcher.find()){
                        jsonObject.put("email", "L'email n'a pas un bon format.");
                    }else if(!userRequest.getEmail().equals(jsonUser.getString("email")) && userRepository.findByEmail(jsonUser.getString("email")) != null){
                        jsonObject.put("email", "L'email existe déjà.");
                    }else if(jsonUser.getString("pseudo").equals("")){
                        jsonObject.put("pseudo", "Le pseudo ne peut pas être vide.");
                    }else if(!userRequest.getPseudo().equals(jsonUser.getString("pseudo")) && userRepository.findByPseudo(jsonUser.getString("pseudo")) != null){
                        jsonObject.put("pseudo", "Le pseudo existe déjà.");
                    }else{
                        userRequest.setPseudo(jsonUser.getString("pseudo"));
                        userRequest.setEmail(jsonUser.getString("email"));
                        final User newUser = userRepository.save(userRequest);

                        jsonObject.put("message", "OK");
                        jsonObject.put("user", newUser.toJSON());
                    }
                }
            } catch (JSONException e) {
                logger.error("CryptoTrade -> "+ e.getMessage());
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        return jsonObject.toString();
    }
}

