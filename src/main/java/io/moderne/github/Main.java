package io.moderne.github;

import org.kohsuke.github.*;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String ghOrigin = args.length == 0 ? "github.com" : args[0];
        GitHub github = GitHubBuilder.fromPropertyFile().build();

        try (FileWriter writer = new FileWriter("repos.csv")) {
            PagedIterator<GHOrganization> orgs = github.listOrganizations()._iterator(10);
            while(orgs.hasNext()) {
                GHOrganization org = orgs.next();
                for (GHRepository repo : org.getRepositories().values()) {
                    writer.write(ghOrigin + "," + org.getName() + "/" + repo.getName() + "," + repo.getDefaultBranch() + "\n");
                }
            }
        }
    }
}
