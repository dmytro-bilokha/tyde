define([
	'leaflet',
	'../accUtils',
],
	function (L, accUtils) {
		'use strict';
		class MapViewModel {

			constructor() {
				this.lastMarker = null;
				this.line = null;
				this.map = null;
				this.pointOptions = {
					radius: 8,
					fillColor: "#ff78ff",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8
				};
				this.lastPointOptions = {
					radius: 8,
					fillColor: "#ff7800",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8
				};

				this.connected = () => {
					accUtils.announce('Map page loaded.');
					document.title = "Map";
					this.map = L.map('map').setView([51.505, -0.09], 13);
					L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
						maxZoom: 19,
						attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
					}).addTo(this.map);
					this.line = L.polyline([]).addTo(this.map);
					this.map.on('click', this.onMapClick);
				};

				this.onMapClick = (e) => {
					this.line.addLatLng(e.latlng);
					if (this.lastMarker !== null) {
						this.lastMarker.setStyle(this.pointOptions);
					}
					const circleMarker = L.circleMarker(e.latlng, this.lastPointOptions);
					circleMarker.bindPopup("Hello, I am the popup.");
					circleMarker.addTo(this.map);
					this.lastMarker = circleMarker;
					this.map.setView(e.latlng, 13);
				};

				this.disconnected = () => {
					// Implement if needed
				};

				this.transitionCompleted = () => {
					// Implement if needed
				};

			}
		}

		return new MapViewModel();

	}
);
