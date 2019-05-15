package org.processmining.contexts.uitopia.packagemanager;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class PMIconCache {

	/*
	 * Cache the retrieved icons. Prevents unnecessary access over the network.
	 */
	private static Map<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();
	private static Map<String, ImageIcon> iconPreviewMap = new HashMap<String, ImageIcon>();

	public static ImageIcon getIcon(PMPackage pack) throws MalformedURLException {
		synchronized (iconMap) {
			/*
			 * Check whether icon already in cache
			 */
			ImageIcon icon = iconMap.get(pack.getDescriptor().getLogoURL());
			if (icon != null) {
				/*
				 * Yes, it is. Return cached icon.
				 */
				return icon;
			}
			/*
			 * No, it is not. Retrieve icon and put in cache.
			 */
			URL logoURL = new URL(pack.getDescriptor().getLogoURL());
			icon = new ImageIcon(logoURL);
			iconMap.put(pack.getDescriptor().getLogoURL(), icon);
			return icon;
		}
	}

	public static ImageIcon getIconPreview(PMPackage pack) {
		synchronized (iconPreviewMap) {
			/*
			 * Check whether icon preview already in cache
			 */
			ImageIcon icon = iconPreviewMap.get(pack.getDescriptor().getLogoURL());
			if (icon != null) {
				/*
				 * Yes, it is. Return cached icon preview.
				 */
				return icon;
			}
			/*
			 * No, it is not. Get icon preview and put in cache.
			 */
			Image image = pack.getPreview(150, 150);
			if (image != null) {
				icon = new ImageIcon(image);
				iconPreviewMap.put(pack.getDescriptor().getLogoURL(), icon);
			}
			return icon;
		}
	}
}
