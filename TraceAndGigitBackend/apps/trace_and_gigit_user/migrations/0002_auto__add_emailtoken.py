# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'EmailToken'
        db.create_table(u'trace_and_gigit_user_emailtoken', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('email', self.gf('django.db.models.fields.EmailField')(unique=True, max_length=75, db_index=True)),
            ('token', self.gf('django.db.models.fields.CharField')(max_length=12, db_index=True)),
            ('expires_at', self.gf('django.db.models.fields.DateTimeField')(default=None, null=True)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['EmailToken'])


    def backwards(self, orm):
        # Deleting model 'EmailToken'
        db.delete_table(u'trace_and_gigit_user_emailtoken')


    models = {
        u'trace_and_gigit_user.clientsecret': {
            'Meta': {'unique_together': "[('os', 'secret')]", 'object_name': 'ClientSecret'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'os': ('django.db.models.fields.CharField', [], {'max_length': '32'}),
            'secret': ('django.db.models.fields.CharField', [], {'max_length': '32'}),
            'service_id': ('django.db.models.fields.CharField', [], {'default': "'traceandgigit'", 'max_length': '255'})
        },
        u'trace_and_gigit_user.device': {
            'Meta': {'unique_together': "[('os', 'make', 'model', 'serial_number', 'profile')]", 'object_name': 'Device'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'device_id': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '255', 'db_index': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'make': ('django.db.models.fields.CharField', [], {'max_length': '64', 'db_index': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'modified_on': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'os': ('django.db.models.fields.CharField', [], {'max_length': '64', 'db_index': 'True'}),
            'os_version': ('django.db.models.fields.CharField', [], {'max_length': '64'}),
            'profile': ('django.db.models.fields.CharField', [], {'max_length': '32'}),
            'resolution': ('django.db.models.fields.CharField', [], {'max_length': '12'}),
            'serial_number': ('django.db.models.fields.CharField', [], {'max_length': '255', 'db_index': 'True'}),
            'service_id': ('django.db.models.fields.CharField', [], {'default': "'traceandgigit'", 'max_length': '255', 'db_index': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['trace_and_gigit_user.User']", 'null': 'True', 'blank': 'True'})
        },
        u'trace_and_gigit_user.emailtoken': {
            'Meta': {'object_name': 'EmailToken'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'email': ('django.db.models.fields.EmailField', [], {'unique': 'True', 'max_length': '75', 'db_index': 'True'}),
            'expires_at': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'token': ('django.db.models.fields.CharField', [], {'max_length': '12', 'db_index': 'True'})
        },
        u'trace_and_gigit_user.session': {
            'Meta': {'object_name': 'Session'},
            'client_key': ('django.db.models.fields.CharField', [], {'max_length': '128', 'unique': 'True', 'null': 'True', 'db_index': 'True'}),
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'device': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['trace_and_gigit_user.Device']", 'null': 'True'}),
            'expires_at': ('django.db.models.fields.DateTimeField', [], {'default': 'None', 'null': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'ip_address': ('django.db.models.fields.IPAddressField', [], {'max_length': '15', 'null': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['trace_and_gigit_user.User']", 'null': 'True'})
        },
        u'trace_and_gigit_user.user': {
            'Meta': {'object_name': 'User'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'dob': ('django.db.models.fields.DateField', [], {'null': 'True'}),
            'email': ('django.db.models.fields.EmailField', [], {'unique': 'True', 'max_length': '75', 'db_index': 'True'}),
            'email_verified': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'first': ('django.db.models.fields.CharField', [], {'db_index': 'True', 'max_length': '64', 'blank': 'True'}),
            'gender': ('django.db.models.fields.CharField', [], {'default': "'N'", 'max_length': '2'}),
            'guest_device': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'related_name': "'guest_user'", 'unique': 'True', 'null': 'True', 'to': u"orm['trace_and_gigit_user.Device']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last': ('django.db.models.fields.CharField', [], {'db_index': 'True', 'max_length': '64', 'blank': 'True'}),
            'modified_on': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'password_hash': ('django.db.models.fields.CharField', [], {'max_length': '128', 'null': 'True'})
        },
        u'trace_and_gigit_user.useremail': {
            'Meta': {'object_name': 'UserEmail'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'email': ('django.db.models.fields.EmailField', [], {'unique': 'True', 'max_length': '75', 'db_index': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_primary': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_verified': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'modified_on': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['trace_and_gigit_user.User']"})
        },
        u'trace_and_gigit_user.usermobile': {
            'Meta': {'object_name': 'UserMobile'},
            'created_on': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'imsi': ('django.db.models.fields.CharField', [], {'max_length': '64', 'null': 'True'}),
            'is_verified': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'mobile': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '32', 'db_index': 'True'}),
            'modified_on': ('django.db.models.fields.DateTimeField', [], {'auto_now': 'True', 'blank': 'True'}),
            'network': ('django.db.models.fields.CharField', [], {'max_length': '12', 'null': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['trace_and_gigit_user.User']"})
        }
    }

    complete_apps = ['trace_and_gigit_user']