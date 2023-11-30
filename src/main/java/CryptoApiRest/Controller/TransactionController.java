package CryptoApiRest.Controller;

import CryptoApiRest.Model.Transaction;
import CryptoApiRest.Model.User;
import CryptoApiRest.Model.Wallet;
import CryptoApiRest.Repositories.TransactionRepository;
import CryptoApiRest.Repositories.WalletRepository;
import CryptoApiRest.Tools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static CryptoApiRest.Tools.getResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin
@RequestMapping("/transactions")
public class TransactionController {
	final static Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    // Add a transaction
    @RequestMapping(value = "/addTransaction", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        Wallet walletUser = walletRepository.findByUserAndCrypto(transaction.getUser(), transaction.getCryptoFrom());

        // Test if crypto from and crypto to are not the same and user has the funds
        if(!transaction.getCryptoFrom().getShortName().equals(transaction.getCryptoTo().getShortName()) && walletUser.getAmount() >= transaction.getAmountFrom()){
            walletUser.setAmount(walletUser.getAmount() - transaction.getAmountFrom());
            walletRepository.save(walletUser);

            walletUser = walletRepository.findByUserAndCrypto(transaction.getUser(), transaction.getCryptoTo());
            if(walletUser == null){ // Si le wallet de la crypto-monnaie n'existe pas
                walletUser = walletRepository.save(new Wallet(0, transaction.getUser(), transaction.getCryptoTo()));
            }
            walletUser.setAmount(transaction.getAmountTo() + walletUser.getAmount());
            walletRepository.save(walletUser);

            return transactionRepository.save(transaction);
        }else{
            return null;
        }
    }

    @RequestMapping(value = "/getAllTransaction/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMoneyByIdUser(@PathVariable(value = "id") long userId) {
        final List<Transaction> transactionList = transactionRepository.findTransactionByUserOrderByIdTransactionDesc(new User(userId));
        final JSONArray jsonArray = new JSONArray();

        try {
            final JSONObject objectListCrypto = new JSONObject(getResponse("GET", Tools.getUrlCrypto() +"/data/all/coinlist?api_key="+ Tools.getApiKeyCrypto()));
            final JSONObject listData = objectListCrypto.getJSONObject("Data");

            for (final Transaction transaction : transactionList) {
                final JSONObject objectReturn = new JSONObject();

                final JSONObject cryptoTo = listData.getJSONObject(transaction.getCryptoTo().getShortName());
                final JSONObject cryptoFrom = listData.getJSONObject(transaction.getCryptoFrom().getShortName());

                objectReturn.put("imageFrom", "https://www.cryptocompare.com" +cryptoFrom.getString("ImageUrl"));
                objectReturn.put("imageTo", "https://www.cryptocompare.com" +cryptoTo.getString("ImageUrl"));
                objectReturn.put("shortNameFrom", transaction.getCryptoFrom().getShortName());
                objectReturn.put("shortNameTo", transaction.getCryptoTo().getShortName());
                objectReturn.put("nameFrom", cryptoFrom.getString("CoinName"));
                objectReturn.put("nameTo", cryptoTo.getString("CoinName"));
                objectReturn.put("amountPriceEur", transaction.getPriceAtTime()+" €");
                objectReturn.put("amountFrom", transaction.getAmountFrom()+" "+transaction.getCryptoFrom().getShortName());
                objectReturn.put("amountTo", transaction.getAmountTo()+" "+transaction.getCryptoTo().getShortName());
                objectReturn.put("date", transaction.getDate());

                jsonArray.put(objectReturn);
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }
		
        return jsonArray.toString();
    }
}

