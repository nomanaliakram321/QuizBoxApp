package noman.quizzapp.models;

public class ModelClass {
    private String name,email,passwoed;

    public ModelClass() {
    }

    public ModelClass(String name, String email, String passwoed) {
        this.name = name;
        this.email = email;
        this.passwoed = passwoed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswoed() {
        return passwoed;
    }

    public void setPasswoed(String passwoed) {
        this.passwoed = passwoed;
    }
}
