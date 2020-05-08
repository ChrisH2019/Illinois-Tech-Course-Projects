from django.contrib.auth.decorators import login_required
from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.urls import reverse
from django.contrib.auth import logout, login, authenticate
from django.shortcuts import render
from django.contrib.auth.forms import UserCreationForm
from .forms import *
from .models import *


def index(request):
    """Redirect Home page."""
    products = Store.inventory.products
    return render(request, 'users/index.html', {'products': products})


def logout_view(request):
    """Log the user out."""
    logout(request)
    return HttpResponseRedirect(reverse('users:index'))


def register(request):
    """Register a new user."""
    if request.method != 'POST':
        form = UserCreationForm()
    else:
        form = UserCreationForm(request.POST)

        if form.is_valid():
            form.save()
            u = form.cleaned_data.get('username')
            p = form.cleaned_data.get('password2')
            user = authenticate(username=u, password=p)
            login(request, user)
            return HttpResponseRedirect(reverse('users:index'))

    return render(request, 'users/register.html', {'form': form})


@login_required
def profile(request):
    """Update the user profile."""
    user = request.user

    current_info = {
        'username': user.username,
        'first_name': user.first_name,
        'last_name': user.last_name,
    }

    if request.method != 'POST':
        form = Profile(current_info)
    else:
        form = Profile(request.POST)

        if form.is_valid():
            if form.cleaned_data['username'] != user.username:
                user.username = form.cleaned_data['username']

            if form.cleaned_data['first_name'] != user.first_name:
                user.username = form.cleaned_data['first_name']

            if form.cleaned_data['last_name'] != user.last_name:
                user.username = form.cleaned_data['last_name']

            user.save()

            return render(request, 'users/profile.html', {'form': form})

    return render(request, 'users/profile.html', {'form': form})


def product(request, product_id):
    """Show Product Detail"""

    identifier = int(product_id)

    product = Store.inventory.products[identifier]

    if product.quantity < 1:
        product.isSoldOut = True
    else:
        product.isSoldOut = False

    products = [product]
    insufficient = False

    if request.method != 'POST':
        form = ProductForm()
    else:
        form = ProductForm(request.POST)

        if form.is_valid():

            quantityRequested = int(form.cleaned_data['quantity'])

            if quantityRequested > product.quantity:
                insufficient = True

            if not insufficient and quantityRequested > 0:
                Store.cart.addProduct(product, quantityRequested)
                return HttpResponseRedirect(reverse('users:cart'))

    return render(request, 'users/product.html', {'products': products, 'form': form, 'insufficient': insufficient})


def cart(request):
    """Show Shopping Cart"""

    products = Store.cart.products

    return render(request, 'users/cart.html', {'products': products})

def checkout(request):
    """Show Checkout Page"""

    products = Store.cart.products
    Store.cart.calculatePrice()
    total = Store.cart.total

    if request.method != 'POST':
        form = CheckoutForm()
    else:
        form = CheckoutForm(request.POST)

        if form.is_valid():

            Store.cart.purchased()
            return render(request, 'users/success.html')

    return render(request, 'users/checkout.html', {'products': products, 'total': total, 'form': form})


def success(request):
    """Show Order Success Page"""

    return render(request, 'users/success.html')
