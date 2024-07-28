define([
  'knockout',
	'ojs/ojarraydataprovider',
	'../pointManager',
	'leaflet',
	'../accUtils',
	'oj-c/select-single',
],
	function (
		ko,
		ArrayDataProvider,
		pointManager,
		L,
		accUtils
	) {
		'use strict';
		class MapViewModel {

			constructor() {

				this.devices = ko.observableArray([]);
				this.deviceProvider = new ArrayDataProvider(this.devices, {
					keyAttributes: 'value',
				});

				this.lastMarker = null;
				this.line = null;
				this.map = null;
				this.pointsLatLng = [];
				this.markers = [];

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
					pointManager.fetchAvailableDevices(this.devices);
					console.log("Map connecting...");
					accUtils.announce('Map page loaded.');
					this.map = L.map('map').setView([52.7297347, 4.436961], 13);
					L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
						maxZoom: 19,
						attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
					}).addTo(this.map);
					this.line = L.polyline(this.pointsLatLng).addTo(this.map);
					pointManager.init(this.onPointsChange);
					console.log("Map connected");
				};

				this.onGpsDeviceChange = (deviceChangeEvent) => {
					pointManager.connect(deviceChangeEvent.detail.value);
				};

				this.onPointsChange = (changes) => {
					for (const change of changes) {
						if (change.status === 'added') {
							const point = change.value;
							const pointLatLng = [point.lat, point.lon];
							this.pointsLatLng.push(pointLatLng);
							if (this.markers.length > 0) {
								const lastMarker = this.markers[this.markers.length - 1];
								lastMarker.setStyle(this.pointOptions);
							}
							const circleMarker = L.circleMarker(pointLatLng, this.lastPointOptions);
							const timestampUtc = new Date(point.timestamp);
							const timestampLocal = new Date(timestampUtc.getTime() - timestampUtc.getTimezoneOffset() * 60 * 1000);
							circleMarker.bindPopup(timestampLocal.toISOString());
							circleMarker.addTo(this.map);
							this.markers.push(circleMarker);
							this.map.setView(pointLatLng, 13);
						} else if (change.status === 'deleted') {
							this.pointsLatLng.splice(change.index, 1);
							this.map.removeLayer(this.markers[change.index]);
							this.markers.splice(change.index, 1);
						}
					}
					this.line.setLatLngs(this.pointsLatLng);
				};

				this.disconnected = () => {
					this.lastMarker = null;
					this.line = null;
					this.map = null;
					pointManager.destruct();
				};

				this.transitionCompleted = () => {
					// Implement if needed
				};

			}
		}

		return new MapViewModel();

	}
);
