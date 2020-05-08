/**
 * @package 	WordPress
 * @subpackage 	Agrofields
 * @version		1.2.9
 * 
 * Visual Content Composer Schortcodes Extend
 * Created by CMSMasters
 * 
 */


/**
 * Posts Slider Extend
 */

var posts_slider_new_fields = {};


for (var id in cmsmsShortcodes.cmsms_posts_slider.fields) {
	if (id === 'amount') {
		delete cmsmsShortcodes.cmsms_posts_slider.fields[id];
	} else if (id === 'columns') {
		delete cmsmsShortcodes.cmsms_posts_slider.fields[id]['depend'];
		
		
		posts_slider_new_fields[id] = cmsmsShortcodes.cmsms_posts_slider.fields[id];
	} else {
		posts_slider_new_fields[id] = cmsmsShortcodes.cmsms_posts_slider.fields[id];
	}
}


cmsmsShortcodes.cmsms_posts_slider.fields = posts_slider_new_fields;



/**
 * Button Extend
 */

var button_new_fields = {};


for (var id in cmsmsShortcodes.cmsms_button.fields) {
	if (id === 'button_font_weight') {
		cmsmsShortcodes.cmsms_button.fields[id]['def'] = '600';
		
		button_new_fields[id] = cmsmsShortcodes.cmsms_button.fields[id];
	} else {
		button_new_fields[id] = cmsmsShortcodes.cmsms_button.fields[id];
	}
}


cmsmsShortcodes.cmsms_button.fields = button_new_fields;



/**
 * Single Pricing Table Items Extend
 */

var pricing_table_item_new_fields = {};


for (var id in cmsmsMultipleShortcodes.cmsms_pricing_table_item.fields) {
	if (id === 'button_font_weight') {
		cmsmsMultipleShortcodes.cmsms_pricing_table_item.fields[id]['def'] = '600';
		
		pricing_table_item_new_fields[id] = cmsmsMultipleShortcodes.cmsms_pricing_table_item.fields[id];
	} else {
		pricing_table_item_new_fields[id] = cmsmsMultipleShortcodes.cmsms_pricing_table_item.fields[id];
	}
}


cmsmsMultipleShortcodes.cmsms_pricing_table_item.fields = pricing_table_item_new_fields;

