import java.util.ArrayList;

public class Pedido {
    private String numeroMesa, valorTotal, data, hora;
    private ArrayList<ItemCardapio> itens;
    private String downloadUrl;
    private boolean isPrinted = false, isPaid = false;

    public String getNumeroMesa() {
        return numeroMesa;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public ArrayList<ItemCardapio> getItens() {
        return itens;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isPrinted() {
        return isPrinted;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
