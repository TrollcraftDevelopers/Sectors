package pl.trollcraft.sectors.messaging;

public interface Request {

    int id();
    String[] process(String from, String[] data);

}
