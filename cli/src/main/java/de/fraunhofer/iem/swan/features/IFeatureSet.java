package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.srm.dataset.Category;
import de.fraunhofer.iem.srm.dataset.Method;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Set;

public interface IFeatureSet {

     void createFeatures();

     ArrayList<Attribute> createAttributes(Set<Category> categories, Set<Method> methods);
    }
