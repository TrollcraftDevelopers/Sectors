package pl.trollcraft.sectors.messaging;

public interface Request {

    byte id();
    String[] process(String[] data);

}
