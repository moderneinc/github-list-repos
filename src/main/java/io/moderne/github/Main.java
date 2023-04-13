package io.moderne.github;

import org.kohsuke.github.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

public class Main {
    public static void main(String[] args) throws IOException {
        String ghOrigin = args.length == 0 ? "github.com" : args[0];
        String ghEndpoint = args.length < 2 ? "https://api.github.com" : args[1];
        GitHub github = GitHubBuilder.fromPropertyFile()
                .withEndpoint(ghEndpoint)
                .build();

        try (FileWriter writer = new FileWriter("repos.csv")) {
            PagedIterator<GHOrganization> orgs = github.listOrganizations()._iterator(10);
            while (orgs.hasNext()) {
                GHOrganization org = orgs.next();
                try {
                    for (GHRepository repo : org.getRepositories().values()) {
                        if (!repo.isArchived() && LocalDate.now().minusYears(2).isBefore(repo.getPushedAt()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate())) {
                            writer.write(org.getLogin() + "/" + repo.getName() + "," + repo.getDefaultBranch() + ",,,,,\n");
                        }
                    }
                } catch (Throwable ignored) {
                    // continue to the next org
                }
            }
        }
    }
}
