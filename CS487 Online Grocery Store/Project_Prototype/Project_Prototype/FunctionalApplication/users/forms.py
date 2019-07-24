from django import forms


class Profile(forms.Form):
    username = forms.RegexField(
        label='Username',
        max_length=30,
        regex=r'^[a-z0-9-_]+$',
        error_messages={
            'required': 'Please enter your name',
            'invalid': 'Invalid alphanumeric characters'
        },
    )

    first_name = forms.RegexField(
        label='First name',
        max_length=30,
        regex=r'^[a-zA-Z-_]+$',
        error_messages={'invalid': 'Alphabetical characters only'},
        required=False,
    )

    last_name = forms.RegexField(
        label='Last name',
        max_length=30,
        regex=r'^[a-zA-Z-_]+$',
        error_messages={'invalid': 'Alphabetical characters only'},
        required=False,
    )


class ProductForm(forms.Form):
    quantity = forms.RegexField(
        label='Quantity',
        max_length=5,
        regex=r'^[0-9]+$',
        error_messages={
            'required': 'Please enter quantity desired',
            'invalid': 'Invalid characters, please enter a number'
        },
    )

class CheckoutForm(forms.Form):
    email = forms.RegexField(
        label='Email',
        max_length=30,
        regex=r'^[0-9a-zA-Z.]+@[0-9a-zA-Z.]+$',
        error_messages={
            'invalid': 'Invalid email address'
        },
        required=True,
    )

    first_name = forms.RegexField(
        label='First Name',
        max_length=30,
        regex=r'^[a-zA-Z]+$',
        error_messages={'invalid': 'Invalid characters'},
        required=True,
    )

    last_name = forms.RegexField(
        label='Last Name',
        max_length=30,
        regex=r'^[a-zA-Z]+$',
        error_messages={'invalid': 'Invalid characters'},
        required=True,
    )

    address_line_1 = forms.RegexField(
        label='Address Line 1',
        max_length=30,
        regex=r'^[a-zA-Z0-9. ]+$',
        error_messages={'invalid': 'Invalid address'},
        required=True,
    )

    address_line_2 = forms.RegexField(
        label='Address Line 2',
        max_length=30,
        regex=r'^[a-zA-Z0-9. ]+$',
        error_messages={'invalid': 'Invalid address'},
        required=False,
    )

    city = forms.RegexField(
        label='City',
        max_length=30,
        regex=r'^[a-zA-Z]+$',
        error_messages={'invalid': 'Invalid characters'},
        required=True,
    )

    state = forms.RegexField(
        label='State',
        max_length=30,
        regex=r'^[a-zA-Z]+$',
        error_messages={'invalid': 'Invalid characters'},
        required=True,
    )

    zip_code = forms.RegexField(
        label='Zip Code',
        max_length=30,
        regex=r'^[0-9]{5}$',
        error_messages={
            'invalid': 'Invalid zip code'
        },
        required=True,
    )

    phone = forms.RegexField(
        label='Phone',
        max_length=30,
        regex=r'^[0-9]{10}$',
        error_messages={
            'invalid': 'Invalid phone number'
        },
        required=False,
    )

    name_on_card = forms.RegexField(
        label='Name on Card',
        max_length=30,
        regex=r'^[a-zA-Z ]+$',
        error_messages={'invalid': 'Invalid characters'},
        required=True,
    )

    card_number = forms.RegexField(
        label='Card Number',
        max_length=30,
        regex=r'^([0-9]{4}\s?){4}$',
        error_messages={
            'invalid': 'Invalid card number'
        },
        required=True,
    )

    exp_month = forms.RegexField(
        label='Expiration Month',
        max_length=30,
        regex=r'^[0-9]{1,2}$',
        error_messages={
            'invalid': 'Invalid expiration month'
        },
        required=True,
    )

    exp_year = forms.RegexField(
        label='Expiration Year',
        max_length=30,
        regex=r'^[0-9]{1,4}$',
        error_messages={
            'invalid': 'Invalid expiration year'
        },
        required=True,
    )

    cvv = forms.RegexField(
        label='CVV',
        max_length=30,
        regex=r'^[0-9]{3}$',
        error_messages={
            'invalid': 'Invalid CVV'
        },
        required=True,
    )
