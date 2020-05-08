"""Define url patterns for users."""

from django.contrib.auth.views import LoginView
from django.conf.urls import url
from . import views

app_name = 'users'
urlpatterns = [
    # Home page.
    url(r'^$', views.index, name='index'),

    # Login page.
    url(r'login/$', LoginView.as_view(template_name='users/login.html'), name='login'),

    # Logout page.
    url(r'logout/$', views.logout_view, name='logout'),

    # Registration page.
    url(r'^register/$', views.register, name='register'),

    # Profile page.
    url(r'profile/$', views.profile, name='profile'),

    # Product page.
    url(r'product/(?P<product_id>\d+)/$', views.product, name='product'),

    # Cart page.
    url(r'cart/$', views.cart, name='cart'),

    # Checkout page.
    url(r'checkout/$', views.checkout, name='checkout'),

    # Order success page.
    url(r'success/$', views.success, name='success'),
]