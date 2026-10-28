package service;

import model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Week 11 MiniBank - StatePersister
 * Handles object serialization and deserialization for Account arrays
 * using ObjectOutputStream and ObjectInputStream inside try-with-resources.
 */
public class StatePersister {

    /**
     * Saves an array of Account objects to the specified file path.
     * Uses ObjectOutputStream inside try-with-resources.
     *
     * @param accounts array of accounts to persist
     * @param file target file path
     * @throws IOException if an I/O error occurs
     */
    public static void save(Account[] accounts, Path file) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    /**
     * Loads an array of Account objects from the specified file path.
     * Uses ObjectInputStream inside try-with-resources.
     *
     * @param file source file path
     * @return deserialized array of Account objects
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (Account[]) ois.readObject();
        }
    }
}
