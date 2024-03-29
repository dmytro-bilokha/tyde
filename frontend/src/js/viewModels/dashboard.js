define([
	'../pointManager',
	'leaflet',
	'../accUtils',
],
	function (pointManager, L, accUtils) {
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
					console.log("Map connecting...");
					accUtils.announce('Map page loaded.');
					document.title = "Map";
					this.map = L.map('map').setView([52.7297347, 4.436961], 13);
					L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
						maxZoom: 19,
						attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
					}).addTo(this.map);
					this.line = L.polyline([]).addTo(this.map);
					pointManager.init(this.onPointsChange);
					console.log("Map connected");
				};

				this.onPointsChange = (changes) => {
					for (const change of changes) {
						if (change.status === 'added') {
							const point = change.value;
							const pointLatLng = [point.lat, point.lon];
							this.line.addLatLng(pointLatLng);
							if (this.lastMarker !== null) {
								this.lastMarker.setStyle(this.pointOptions);
							}
							const circleMarker = L.circleMarker(pointLatLng, this.lastPointOptions);
							circleMarker.bindPopup(point.timestamp);
							circleMarker.addTo(this.map);
							this.lastMarker = circleMarker;
							this.map.setView(pointLatLng, 13);
						}
					}
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
