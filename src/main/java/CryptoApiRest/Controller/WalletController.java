package CryptoApiRest.Controller;

import CryptoApiRest.Exception.ResourceNotFoundException;
import CryptoApiRest.Model.Crypto;
import CryptoApiRest.Model.User;
import CryptoApiRest.Model.Wallet;
import CryptoApiRest.Repositories.WalletRepository;
import CryptoApiRest.Tools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static CryptoApiRest.Tools.getResponse;

@RestController
@CrossOrigin
@RequestMapping("/wallets")
public class WalletController {
    final static Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/getMoney/{id}")
    public List <Wallet> getMoneyByIdUser(@PathVariable(value = "id") long userId) {
        return this.walletRepository.findWalletByUser(new User(userId));
    }

    @GetMapping("/{id}")
    public Wallet getWalletById(@PathVariable(value = "id") long walletId) {
        return this.walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id :" + walletId));
    }

    @GetMapping("/getMoney/{id}/{shortName}")
    public Wallet getWalletByIdAndByShortNameCrypto(@PathVariable(value = "id") long userId, @PathVariable(value = "shortName") String shortName) {
        return this.walletRepository.findByUserAndCrypto(new User(userId), new Crypto(shortName));
    }

    @RequestMapping(value = "/getClassement", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getClassement() {
        final Map<String, Double> listUser = new HashMap<>();
        final JSONArray jsonArray = new JSONArray();

        final List<Wallet> listWallet = walletRepository.getAllWalletOrderByCrypto();
        String nameCryptoTemp = "";
        double priceEurUnitForCryptoTemp = 0.0;

        try {
            for(final Wallet wallet : listWallet){
                if(!nameCryptoTemp.equals(wallet.getCrypto().getShortName())){
                    nameCryptoTemp = wallet.getCrypto().getShortName();

                    priceEurUnitForCryptoTemp = new JSONObject(getResponse("GET", Tools.getUrlCrypto() +"/data/pricemulti?api_key="+ Tools.getApiKeyCrypto() +"&fsyms="+ nameCryptoTemp +"&tsyms=EUR")).getJSONObject(nameCryptoTemp).getDouble("EUR");
                }

                final String username = wallet.getUser().getPseudo();
                if(listUser.get(username) == null){
                    listUser.put(username, priceEurUnitForCryptoTemp * wallet.getAmount());
                }else{
                    listUser.put(username, listUser.get(username) + (priceEurUnitForCryptoTemp * wallet.getAmount()));
                }
            }

            // Triage
            List<Map.Entry<String, Double>> list = new ArrayList<>(listUser.entrySet());
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            for(int i = 0; i < list.size(); i++){
                JSONObject j = new JSONObject();
                j.put("userName", list.get(i).getKey());
                j.put("priceInEur", list.get(i).getValue());
                jsonArray.put(i, j);
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        return jsonArray.toString();
    }

}