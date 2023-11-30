package CryptoApiRest.Controller;

import CryptoApiRest.Model.Crypto;
import CryptoApiRest.Model.Wallet;
import CryptoApiRest.Repositories.CryptoRepository;
import CryptoApiRest.Repositories.WalletRepository;
import CryptoApiRest.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static CryptoApiRest.Tools.*;

@RestController
@CrossOrigin
@RequestMapping("/cryptos")
public class CryptoController {
    final static Logger logger = LoggerFactory.getLogger(CryptoController.class);

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    public CryptoController(final CryptoRepository cryptoRepository, final WalletRepository walletRepository){
        launch(cryptoRepository, walletRepository);
    }

    // get all crypto
    @GetMapping
    public List <Crypto> getAllCrypto() {
        return cryptoRepository.findAll();
    }

    /*
    Toute les minutes on envoi cette requête : https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=EUR
    final String url = urlCrypto+ "/data/pricemulti?fsyms=BTC,ETH,ICP&tsyms=EUR";

    Boucle sur chaque crypto afin de faire un calcul :
        ( (x - y) / y ) * 100 = v
        y = prix de la base de données
        x = prix de la request
        v = pourcentage positif/négatif

        Mettre à jour du prix de la request vers la db

        Si le pourcentage est plus grand que 1% ou plus petit que -1%
            Alors on envoi au utilisateurs ayant la crypto dans son wallet qui est plus grand que 0.
            Si l'utilisateur à notification non null alors on envoi la notification
     */
    public void launch(final CryptoRepository cryptoRepository, final WalletRepository walletRepository){
        final List<Crypto> listCryptoObject = cryptoRepository.findAll();
        final StringJoiner listCrypto = new StringJoiner(",");
        for(final Crypto crypto : listCryptoObject){
            listCrypto.add(crypto.getShortName());
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final JSONObject jsonObject = new JSONObject(getResponse("GET", Tools.getUrlCrypto() +"/data/pricemulti?api_key="+ Tools.getApiKeyCrypto() +"&fsyms="+ listCrypto +"&tsyms=EUR"));

                    for(final Crypto crypto : listCryptoObject){
                        final double priceForOneCrypto = jsonObject.getJSONObject(crypto.getShortName()).getDouble("EUR");
                        final Crypto cryptoObject = cryptoRepository.findByShortName(crypto.getShortName());

                        if(cryptoObject != null){
                            final double pourcentCrypto = ((priceForOneCrypto - cryptoObject.getCurrentValue()) / cryptoObject.getCurrentValue()) * 100;

                            cryptoObject.setCurrentValue(priceForOneCrypto);
                            cryptoRepository.save(cryptoObject);

                            if(pourcentCrypto > 1 || pourcentCrypto < -1){
                                final List<Wallet> wallets = walletRepository.getAllWalletByCrypto(crypto.getShortName());

                                for(final Wallet wallet : wallets){
                                    // Android
                                    if(wallet.getUser().getNotification() != null){
                                        sendNotification("Alerte prix", "La crypto "+ crypto.getShortName() +" a "+ (pourcentCrypto > 0 ? "augmenté": "baissé") +" de plus de 1% !", wallet.getUser().getNotification());
                                    }

                                    // Web
                                    if(wallet.getUser().getNotificationWeb() != null){
                                        sendNotification("Alerte prix", "La crypto "+ crypto.getShortName() +" a "+ (pourcentCrypto > 0 ? "augmenté": "baissé") +" de plus de 1% !", wallet.getUser().getNotificationWeb());
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("CryptoTrade -> "+ e.getMessage());
                }
            }
        }, 0, 60000);
    }
}

