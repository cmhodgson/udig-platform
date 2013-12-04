/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel.internal;

import java.io.IOException;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Utility class.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class PrecisionToolsUtil {

    private PrecisionToolsUtil() {

        // utility class pattern
    }

    /**
     * Search if there is a feature under cursor position. If snap is off, search on a little bbox
     * around cursor, if it's on, search around an accurate bbox similar to the snap area.
     * 
     * @return True if its a feature under the cursor position, false otherwise.
     */
    public static boolean isFeatureUnderCursor(EditToolHandler handler, MapMouseEvent e) {

        int snapRadious = 5;
        // if snap is on, set an accurate snapRadious to use later for
        // getting
        // its area.
        if (PreferenceUtil.instance().getSnapBehaviour() != SnapBehaviour.OFF) {
            snapRadious = PreferenceUtil.instance().getSnappingRadius()
                    + (PreferenceUtil.instance().getSnappingRadius() / 2);
        }

        // transforms the bbox to the layer crs
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(e.getPoint(), snapRadious);
        try {
            bbox = bbox.transform(handler.getEditLayer().getCRS(), true);
        } catch (TransformException te) {
            te.printStackTrace();
        } catch (FactoryException fe) {
            fe.printStackTrace();
        }

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
        FeatureIterator<SimpleFeature> iterator = null;

        try {
            Class<? extends Filter> filterType = BBOX.class;
            FeatureStore<SimpleFeatureType, SimpleFeature> store = handler.getEditLayer()
                    .getResource(FeatureStore.class, null);
            Filter createBBoxFilter = createBBoxFilter(bbox, handler.getEditLayer(), filterType);
            collection = store.getFeatures(createBBoxFilter);
            iterator = collection.features();
            if (iterator.hasNext()) {
                return true;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (iterator != null) {
                collection.close(iterator);
            }
        }
        return false;
    }

    /**
     * Creates A geometry filter for the given layer.
     * 
     * @param boundingBox in the same crs as the viewport model.
     * @return a Geometry filter in the correct CRS or null if an exception occurs.
     */
    private static Filter createBBoxFilter(ReferencedEnvelope boundingBox, ILayer layer,
            Class<? extends Filter> filterType) {
        FilterFactory2 factory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        if (!layer.hasResource(FeatureSource.class))
            return Filter.EXCLUDE;
        try {

            SimpleFeatureType schema = layer.getSchema();
            Name geom = getGeometryAttDescriptor(schema).getName();

            Filter bboxFilter = factory.bbox(factory.property(geom), boundingBox);

            return bboxFilter;
        } catch (Exception e) {
            ProjectPlugin.getPlugin().log(e);
            return Filter.EXCLUDE;
        }
    }

    private static GeometryDescriptor getGeometryAttDescriptor(SimpleFeatureType schema) {
        return schema.getGeometryDescriptor();
    }

    /**
     * Get the feature under the cursor, the first one if there are more than one. Return null if
     * there isn't feature under the cursor. TODO improve the case when there are more than 1
     * feature under the cursor.
     * 
     * @param handler
     * @param e
     * @return
     */
    public static SimpleFeature getFeatureUnderCursor(EditToolHandler handler, MapMouseEvent e) {

        int snapRadious = 5;
        // if snap is on, set an accurate snapRadious to use later for
        // getting
        // its area.
        if (PreferenceUtil.instance().getSnapBehaviour() != SnapBehaviour.OFF) {
            snapRadious = PreferenceUtil.instance().getSnappingRadius()
                    + (PreferenceUtil.instance().getSnappingRadius() / 2);
        }

        // transforms the bbox to the layer crs
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(e.getPoint(), snapRadious);
        try {
            bbox = bbox.transform(handler.getEditLayer().getCRS(), true);
        } catch (TransformException te) {
            te.printStackTrace();
        } catch (FactoryException fe) {
            fe.printStackTrace();
        }

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
        FeatureIterator<SimpleFeature> iterator = null;

        try {
            Class<? extends Filter> filterType = BBOX.class;
            FeatureStore<SimpleFeatureType, SimpleFeature> store = handler.getEditLayer()
                    .getResource(FeatureStore.class, null);
            Filter createBBoxFilter = createBBoxFilter(bbox, handler.getEditLayer(), filterType);
            collection = store.getFeatures(createBBoxFilter);
            iterator = collection.features();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (iterator != null) {
                collection.close(iterator);
            }
        }

        return null;
    }
}
