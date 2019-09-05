from woocommerce import API

wcapi = API(
    url="https://elr.qji.mybluehost.me",
    consumer_key="ck_0875e96de82a9573f9e4213bfd44c3e191b0a273",
    consumer_secret="cs_1bf33a5913a9ae4cfb6addbe0548a64e75483f80",
    version="wc/v3"
)

def RetrieveAllOrders():
	return print(wcapi.get("orders").json())

def RetrieveSpecificOrder(ordernumber):

	return print(wcapi.get('orders/%s' %ordernumber).json())

def UpdateOrder(status,ordernumber):
	str(status)
	data = {
		"status": "%s" % status
	}

	return print(wcapi.put('orders/%s' %ordernumber, data).json())

def RetrieveAllProducts():
	return print(wcapi.get("products").json())


def RetrieveSpecificProduct(productnumber):

	return print(print(wcapi.get('products/%s' %productnumber).json()))


#RetrieveAllOrders()

#RetrieveSpecificOrder(2193)

#UpdateOrder("processing", 2193)

#RetrieveAllProducts()

#RetrieveSpecificProduct(975)