package CryptoApiRest.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idContact;

    @NotNull
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "subject")
    private String subject;

    @NotNull
    @Column(name = "content")
    private String content;

    public Contact() {
    }

    public Contact(final String email, final String subject, final String content) {
        super();
        this.email = email;
        this.subject = subject;
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}