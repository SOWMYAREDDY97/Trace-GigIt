from django import forms
import datetime
import logging


class InitialDefaultForm(forms.Form):
    def clean(self):
        cleaned_data = super(InitialDefaultForm, self).clean()
        # if data is not provided for some fields and those fields have an
        # initial value, then set the values to initial value
        for name in self.fields:
            if not self[name].html_name in self.data and self.fields[name].initial is not None:
                cleaned_data[name] = self.fields[name].initial
        return cleaned_data

def strip_unsafe_html(text):
    if not text:
        return text
    unsafe_chars = ('<', '>', "'", '"', '&')
    for c in unsafe_chars:
        text = text.replace(c, ' ')
    return text

class BaseForm(InitialDefaultForm):
    pretty = forms.IntegerField(required=False, initial=0)



class SignUpForm(BaseForm):
    mobile = forms.CharField(max_length=20)
    password = forms.CharField(min_length=6, max_length=32)
    first = forms.CharField(max_length=32, required=False)
    last = forms.CharField(max_length=32, required=False)
    gender = forms.CharField(max_length=1, required=False)

    def clean_gender(self):
        gender = self.cleaned_data.get('gender')
        if not gender:
            self.cleaned_data['gender'] = 'N'    # Not specified
        elif not gender in ('M', 'F'):
            raise forms.ValidationError("Invalid Gender")
        return self.cleaned_data['gender']

    def clean(self):
        cleaned_data = super(SignUpForm, self).clean()
        password = cleaned_data.get('password')
        password2 = cleaned_data.get('password2')
        if password != password2:
            raise forms.ValidationError("Passwords didn't match")
        # clean up char fields
        for field in ('first', 'last'):
            if cleaned_data.get(field):
                cleaned_data[field] = strip_unsafe_html(cleaned_data[field])
        return cleaned_data