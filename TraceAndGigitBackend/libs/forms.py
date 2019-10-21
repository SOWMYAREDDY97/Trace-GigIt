from django import forms

class InitialDefaultForm(forms.Form):
    def clean(self):
        cleaned_data = super(InitialDefaultForm, self).clean()
        # if data is not provided for some fields and those fields have an
        # initial value, then set the values to initial value
        for name in self.fields:
            if not self[name].html_name in self.data and self.fields[name].initial is not None:
                cleaned_data[name] = self.fields[name].initial
        return cleaned_data
