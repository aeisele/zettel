package com.andreaseisele.zettel.cli.store;

import com.andreaseisele.zettel.core.credential.data.UsernamePasswordCredential;
import com.andreaseisele.zettel.core.credential.simple.SimpleCredentialStore;

import java.nio.file.Paths;
import java.util.Scanner;

public class StoreUtil {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: StoreUtil filename entry-key");
            System.exit(-1);
        }

        System.out.println("master pw?");
        Scanner scanner = new Scanner(System.in);
        final String masterPassword = scanner.nextLine();

        final SimpleCredentialStore credentialStore = SimpleCredentialStore.createNew(masterPassword.toCharArray());

        System.out.println("username?");
        final String username = scanner.nextLine();
        System.out.println("password?");
        final String password = scanner.nextLine();

        credentialStore.put(args[1], new UsernamePasswordCredential(username.toCharArray(), password.toCharArray()));
        credentialStore.saveToFile(Paths.get(args[0]));

        System.out.println("done");
    }

}
