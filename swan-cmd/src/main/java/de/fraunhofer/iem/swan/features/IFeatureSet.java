package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface IFeatureSet {

     void createFeatures();

     ArrayList<Attribute> createAttributes(Set<Category> categories, Set<Method> methods);
    }
