define([
  'knockout',
	'ojs/ojarraydataprovider',
	'../pointManager',
	'leaflet',
	'../accUtils',
	'oj-c/select-single',
  'ojs/ojformlayout',
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
				this.selectedGpsDeviceId = ko.observable();

				this.timePeriods = [
					{ value: 3, label: '3 minutes' },
					{ value: 60, label: '1 hour' },
					{ value: 120, label: '2 hours' },
					{ value: 600, label: '10 hours' },
					{ value: 1440, label: '24 hours' }
				];
				this.timePeriodProvider = new ArrayDataProvider(this.timePeriods, {
					keyAttributes: 'value',
				});
				this.selectedPeriod = ko.observable(120);

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
					accUtils.announce('Map page loaded.');
					this.map = L.map('map', { zoomControl: false }).setView([52.374450758981276, 4.895229400019448], 13);
					L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
						maxZoom: 19,
						attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
					}).addTo(this.map);
					this.line = L.polyline(this.pointsLatLng).addTo(this.map);
					pointManager.init(this.onPointsChange);
				};

				this.onGpsDeviceChange = (deviceChangeEvent) => {
					pointManager.connect(deviceChangeEvent.detail.value, this.selectedPeriod());
				};

				this.onTimePeriodChange = (periodChangeEvent) => {
					pointManager.connect(this.selectedGpsDeviceId(), periodChangeEvent.detail.value);
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
							this.map.setView(pointLatLng);
						} else if (change.status === 'deleted') {
							this.pointsLatLng.shift();
							const marker = this.markers.shift();
							this.map.removeLayer(marker);
						} else {
							alert(`Unrecognize points change status: ${change.status}`);
						}
						this.line.setLatLngs(this.pointsLatLng);
					}
				};

				this.disconnected = () => {
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
