from django.db import models


class Product:

	def __init__(self, name, ID, price, quantity):
		self.name = name
		self.ID = ID
		self.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
		self.price = price
		self.quantity = quantity
		self.isSoldOut = False

	def decrement(self, value):
		self.quantity -= value


class Inventory:

	def __init__(self):
		products = []
		products.append(Product("Apple", 0, "0.89", 14))
		products.append(Product("Banana", 1, "0.59", 3))
		products.append(Product("Orange", 2, "1.29", 9))
		products.append(Product("Watermelon", 3, "2.49", 7))
		products.append(Product("Squash", 4, "3.39", 5))
		products.append(Product("Kale", 5, "4.99", 0))
		products.append(Product("Wheat Grass", 6, "0.99", 14))
		products.append(Product("Blackberry", 7, "4.79", 11))
		self.products = products

	def removeProduct(self, purchased):

		for product in self.products:
			if product.ID == purchased.ID:
				product.quantity -= purchased.quantity
				break

class Cart:

	def __init__(self):
		self.products = []
		self.total = 0

	def addProduct(self, newProduct, quantity):

		found = False

		for product in self.products:
			if product.ID == newProduct.ID:
				product.quantity += quantity
				found = True
				break

		if not found:
			self.products.append(Product(newProduct.name, newProduct.ID, newProduct.price, quantity))

	def calculatePrice(self):

		self.total = 0
		for product in self.products:
			self.total = self.total + float(product.price) * float(product.quantity)
		self.total = round(self.total, 2)

	def purchased(self):

		for product in self.products:
			Store.inventory.removeProduct(product)

		self.products = []

class Store:

	inventory = Inventory()
	cart = Cart()







