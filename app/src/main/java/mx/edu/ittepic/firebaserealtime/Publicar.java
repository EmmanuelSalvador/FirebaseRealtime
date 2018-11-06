package mx.edu.ittepic.firebaserealtime;

public class Publicar {
    private String texto;
    public Publicar (){
    }
    public Publicar (String t){
        this.texto=t;
    }
    public String getTexto(){
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
