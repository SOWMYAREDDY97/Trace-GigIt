# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'customer'
        db.create_table(u'login_customer', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('first', self.gf('django.db.models.fields.CharField')(db_index=True, max_length=64, blank=True)),
            ('last', self.gf('django.db.models.fields.CharField')(db_index=True, max_length=64, blank=True)),
            ('password_hash', self.gf('django.db.models.fields.CharField')(max_length=128, null=True)),
            ('mobile', self.gf('django.db.models.fields.CharField')(max_length=32, null=True)),
            ('gender', self.gf('django.db.models.fields.CharField')(default='N', max_length=2)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('modified_on', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'login', ['customer'])


    def backwards(self, orm):
        # Deleting model 'customer'
        db.delete_table(u'login_customer')


    models = {
        u'login.customer': {
            'Meta': {'object_name': 'customer'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'first': ('django.db.models.fields.CharField', [], {'db_index': 'True', 'max_length': '64', 'blank': 'True'}),
            'gender': ('django.db.models.fields.CharField', [], {'default': "'N'", 'max_length': '2'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last': ('django.db.models.fields.CharField', [], {'db_index': 'True', 'max_length': '64', 'blank': 'True'}),
            'mobile': ('django.db.models.fields.CharField', [], {'max_length': '32', 'null': 'True'}),
            'modified_on': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'password_hash': ('django.db.models.fields.CharField', [], {'max_length': '128', 'null': 'True'})
        }
    }

    complete_apps = ['login']