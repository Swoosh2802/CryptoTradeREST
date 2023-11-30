package CryptoApiRest.Controller;

import CryptoApiRest.Model.Contact;
import CryptoApiRest.Repositories.ContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactRepository contactRepository;
    
    @RequestMapping(value = "/send", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Contact contact(@RequestBody Contact contact){
        return contactRepository.save(contact);
    }
}

