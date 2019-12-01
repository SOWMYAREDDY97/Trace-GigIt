# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'ClientSecret'
        db.create_table(u'trace_and_gigit_user_clientsecret', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('os', self.gf('django.db.models.fields.CharField')(max_length=32)),
            ('secret', self.gf('django.db.models.fields.CharField')(max_length=32)),
            ('service_id', self.gf('django.db.models.fields.CharField')(default='traceandgigit', max_length=255)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['ClientSecret'])

        # Adding unique constraint on 'ClientSecret', fields ['os', 'secret']
        db.create_unique(u'trace_and_gigit_user_clientsecret', ['os', 'secret'])

        # Adding model 'User'
        db.create_table(u'trace_and_gigit_user_user', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('first', self.gf('django.db.models.fields.CharField')(db_index=True, max_length=64, blank=True)),
            ('last', self.gf('django.db.models.fields.CharField')(db_index=True, max_length=64, blank=True)),
            ('password_hash', self.gf('django.db.models.fields.CharField')(max_length=128, null=True)),
            ('email', self.gf('django.db.models.fields.EmailField')(unique=True, max_length=75, db_index=True)),
            ('email_verified', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('dob', self.gf('django.db.models.fields.DateField')(null=True)),
            ('gender', self.gf('django.db.models.fields.CharField')(default='N', max_length=2)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('modified_on', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
            ('guest_device', self.gf('django.db.models.fields.related.ForeignKey')(default=None, related_name='guest_user', unique=True, null=True, to=orm['trace_and_gigit_user.Device'])),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['User'])

        # Adding model 'UserMobile'
        db.create_table(u'trace_and_gigit_user_usermobile', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('mobile', self.gf('django.db.models.fields.CharField')(unique=True, max_length=32, db_index=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['trace_and_gigit_user.User'])),
            ('is_verified', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('network', self.gf('django.db.models.fields.CharField')(max_length=12, null=True)),
            ('imsi', self.gf('django.db.models.fields.CharField')(max_length=64, null=True)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('modified_on', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['UserMobile'])

        # Adding model 'Device'
        db.create_table(u'trace_and_gigit_user_device', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('os', self.gf('django.db.models.fields.CharField')(max_length=64, db_index=True)),
            ('os_version', self.gf('django.db.models.fields.CharField')(max_length=64)),
            ('make', self.gf('django.db.models.fields.CharField')(max_length=64, db_index=True)),
            ('model', self.gf('django.db.models.fields.CharField')(max_length=128)),
            ('resolution', self.gf('django.db.models.fields.CharField')(max_length=12)),
            ('serial_number', self.gf('django.db.models.fields.CharField')(max_length=255, db_index=True)),
            ('profile', self.gf('django.db.models.fields.CharField')(max_length=32)),
            ('device_id', self.gf('django.db.models.fields.CharField')(unique=True, max_length=255, db_index=True)),
            ('service_id', self.gf('django.db.models.fields.CharField')(default='traceandgigit', max_length=255, db_index=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['trace_and_gigit_user.User'], null=True, blank=True)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('modified_on', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['Device'])

        # Adding unique constraint on 'Device', fields ['os', 'make', 'model', 'serial_number', 'profile']
        db.create_unique(u'trace_and_gigit_user_device', ['os', 'make', 'model', 'serial_number', 'profile'])

        # Adding model 'Session'
        db.create_table(u'trace_and_gigit_user_session', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('device', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['trace_and_gigit_user.Device'], null=True)),
            ('client_key', self.gf('django.db.models.fields.CharField')(max_length=128, unique=True, null=True, db_index=True)),
            ('expires_at', self.gf('django.db.models.fields.DateTimeField')(default=None, null=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['trace_and_gigit_user.User'], null=True)),
            ('ip_address', self.gf('django.db.models.fields.IPAddressField')(max_length=15, null=True)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['Session'])

        # Adding model 'UserEmail'
        db.create_table(u'trace_and_gigit_user_useremail', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('email', self.gf('django.db.models.fields.EmailField')(unique=True, max_length=75, db_index=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['trace_and_gigit_user.User'])),
            ('is_verified', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('is_primary', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('created_on', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('modified_on', self.gf('django.db.models.fields.DateTimeField')(auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'trace_and_gigit_user', ['UserEmail'])


    def backwards(self, orm):
        # Removing unique constraint on 'Device', fields ['os', 'make', 'model', 'serial_number', 'profile']
        db.delete_unique(u'trace_and_gigit_user_device', ['os', 'make', 'model', 'serial_number', 'profile'])

        # Removing unique constraint on 'ClientSecret', fields ['os', 'secret']
        db.delete_unique(u'trace_and_gigit_user_clientsecret', ['os', 'secret'])

        # Deleting model 'ClientSecret'
        db.delete_table(u'trace_and_gigit_user_clientsecret')

        # Deleting model 'User'
        db.delete_table(u'trace_and_gigit_user_user')

        # Deleting model 'UserMobile'
        db.delete_table(u'trace_and_gigit_user_usermobile')

        # Deleting model 'Device'
        db.delete_table(u'trace_and_gigit_user_device')

        # Deleting model 'Session'
        db.delete_table(u'trace_and_gigit_user_session')

        # Deleting model 'UserEmail'
        db.delete_table(u'trace_and_gigit_user_useremail')


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