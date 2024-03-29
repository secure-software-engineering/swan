package de.fraunhofer.iem.swan.io;

import dev.jeka.core.api.depmanagement.*;
import dev.jeka.core.api.depmanagement.resolution.JkDependencyResolver;
import dev.jeka.core.api.depmanagement.resolution.JkResolvedDependencyNode;
import edu.stanford.nlp.util.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Oshando Johnson on 15.09.20
 */
public class DependencyManager {

    private JkDependencyResolver resolver;

    DependencyManager() {
        resolver = JkDependencyResolver.of().addRepos(JkRepo.ofMavenCentral());
    }

    /**
     * Returns the source JAR for a module
     *
     * @param module string for the module
     * @return list of path for the source jars
     */
    public List<Path> getSourceJar(String module) {

        JkDependencySet sources = JkDependencySet.of()
                .and(module);

        return resolver.resolve(sources).getFiles().getEntries();
    }

    public String getDependencies(String moduleDescription) {
        JkDependencySet deps = JkDependencySet.of()
                .and(moduleDescription);

         resolver = JkDependencyResolver.of().addRepos(JkRepo.ofMavenCentral());

        //Get list of dependencies for module
        List<JkResolvedDependencyNode> nodes = resolver.resolve(deps).getDependencyTree().toFlattenList();

        Set<String> dependencies = new HashSet<>();

        for (JkResolvedDependencyNode node : nodes)
            dependencies.add(node.getModuleInfo().getModuleId().toString()
                    + ":" + node.getModuleInfo().getResolvedVersion().toString());


        List<Path> sourcesPath = new ArrayList<>();
        for (String dependency : dependencies) {
            for (Path path : getSourceJar(dependency)){
               sourcesPath.add(path);
            }
        }

        return StringUtils.join(sourcesPath,":");
    }

    public static void main(String[] args) {

        DependencyManager manager = new DependencyManager();

        System.out.println(manager.getDependencies("org.hibernate:hibernate-core:5.2.10.Final"));
    }
}
