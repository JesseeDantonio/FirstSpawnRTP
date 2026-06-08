package fr.jessee.firstSpawnRTP.api;

import fr.jessee.firstSpawnRTP.api.iface.FirstSpawnRtpApi;

public final class FirstSpawnRtpProvider {

    private static FirstSpawnRtpApi instance = null;

    // Méthode pour les autres développeurs (pour récupérer l'API)
    public static FirstSpawnRtpApi get() {
        if (instance == null) {
            throw new IllegalStateException("L'API FirstSpawnRTP n'est pas encore chargée !");
        }
        return instance;
    }

    // Méthode interne pour le module 'core' (pour enregistrer l'API)
    public static void register(FirstSpawnRtpApi api) {
        FirstSpawnRtpProvider.instance = api;
    }

    // Empêche l'instanciation
    private FirstSpawnRtpProvider() {}
}
